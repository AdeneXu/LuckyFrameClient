package luckyclient.caserun.exwebdriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.jenkinsapi.BuildingInitialization;
import luckyclient.jenkinsapi.RestartServerInitialization;
import luckyclient.mail.HtmlMail;
import luckyclient.mail.MailSendInitialization;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * 
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class WebTestControl {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             ����̨ģʽ���ȼƻ�ִ������
	 */

	public static void manualExecutionPlan(String planname) {
		// ������־�����ݿ�
		DbLink.exetype = 1;
		String taskid = "888888";
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForLocal();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("��ʼ��WebDriver�����쳣��",e);
		}
		LogOperation caselog = new LogOperation();
		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<ProjectCaseParams>();
		if (testCases.size() != 0) {
			pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}
		LogUtil.APP.info("��ǰ�ƻ��ж�ȡ����������{}����",testCases.size());
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			LogUtil.APP.info("��ʼִ�е�{}������:��{}��......",i,testcase.getCaseSign());
			try {
				WebCaseExecution.caseExcution(testcase, steps, taskid, wd, caselog, pcplist);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
			}
			LogUtil.APP.info("��ǰ����:��{}��ִ�����......������һ��",testcase.getCaseSign());
		}
		LogUtil.APP.info("��ǰ��Ŀ���Լƻ��е������Ѿ�ȫ��ִ�����...");
		// �ر������
		wd.quit();
	}

	public static void taskExecutionPlan(TaskExecute task) throws InterruptedException {
		// ��¼��־�����ݿ�
		DbLink.exetype = 0;
		String taskid = task.getTaskId().toString();
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		List<ProjectCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(task.getProjectId().toString());
		TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(task.getTaskId());
		String projectname = taskScheduling.getProject().getProjectName();
		task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		String jobname = taskScheduling.getSchedulingName();
		int drivertype = LogOperation.querydrivertype(taskid);
		int[] tastcount = null;
		// �ж��Ƿ�Ҫ�Զ�����TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// �ж��Ƿ񹹽��Ƿ�ɹ�
			if (buildstatus.indexOf("Status:true") > -1) {
				WebDriver wd = null;
				try {
					wd = WebDriverInitialization.setWebDriverForTask(drivertype);
				} catch (WebDriverException e1) {
					LogUtil.APP.error("��ʼ��WebDriver���� WebDriverException��", e1);
				} catch (IOException e2) {
					LogUtil.APP.error("��ʼ��WebDriver���� IOException��", e2);
				}
				LogOperation caselog = new LogOperation();

				List<ProjectCase> cases = GetServerAPI.getCasesbyplanId(taskScheduling.getPlanId());
				LogUtil.APP.info("��ǰ�ƻ���{}���й��С�{}��������������...",task.getTaskName(),cases.size());
				LogOperation.updateTaskExecuteStatus(taskid, cases.size());

				for (ProjectCase testcase : cases) {
					List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
					if (steps.size() == 0) {
						continue;
					}
					LogUtil.APP.info("��ʼִ������:��{}��......",testcase.getCaseSign());
					try {
						// ���뿪ʼִ�е�����
						caselog.insertTaskCaseExecute(taskid, taskScheduling.getProjectId(),testcase.getCaseId(),testcase.getCaseSign(), testcase.getCaseName(), 4);
						WebCaseExecution.caseExcution(testcase, steps, taskid, wd, caselog, pcplist);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
					}
					LogUtil.APP.info("��ǰ����:��{}��ִ�����......������һ��",testcase.getCaseSign());
				}
				tastcount = LogOperation.updateTaskExecuteData(taskid, cases.size());

				String testtime = LogOperation.getTestTime(taskid);
				LogUtil.APP.info("��ǰ��Ŀ��{}�����Լƻ��е������Ѿ�ȫ��ִ�����...",projectname);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime, jobname),
						taskid, taskScheduling, tastcount);
				// �ر������
				wd.quit();
			} else {
				LogUtil.APP.warn("��Ŀ����ʧ�ܣ��Զ��������Զ��˳�����ǰ��JENKINS�м����Ŀ���������");
				MailSendInitialization.sendMailInitialization(jobname, "������Ŀ������ʧ�ܣ��Զ��������Զ��˳�����ǰȥJENKINS�鿴���������", taskid,
						taskScheduling, tastcount);
			}
		} else {
			LogUtil.APP.warn("��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������");
			MailSendInitialization.sendMailInitialization(jobname, "��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������", taskid,
					taskScheduling, tastcount);
		}
	}

}
