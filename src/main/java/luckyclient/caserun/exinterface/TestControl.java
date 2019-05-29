package luckyclient.caserun.exinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
 * @ClassName: TestControl
 * @Description: ����ɨ��ָ����Ŀ�������ű��������ýű��еķ��� @author�� seagull
 * @date 2014��8��24�� ����9:29:40
 * 
 */
public class TestControl {
	public static String TASKID = "NULL";
	public static int THREAD_COUNT = 0;

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             ����̨ģʽ���ȼƻ�ִ������
	 */

	public static void manualExecutionPlan(String planname) throws Exception {
		DbLink.exetype = 1;
		int threadcount = 10;
		// �����̳߳أ����߳�ִ������
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<ProjectCaseParams>();
		if (testCases.size() != 0) {
			pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}

		String taskid = "888888";
		// ��ʼ��д��������Լ���־ģ��
		LogOperation caselog = new LogOperation();
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				LogUtil.APP.warn("������" + testcase.getCaseSign() + "��û���ҵ����裬ֱ�����������飡");
				caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "��������û���ҵ����裬����", "error", "1", "");
				continue;
			}
			THREAD_COUNT++; // ���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
			threadExecute.execute(new ThreadForExecuteCase(testcase, steps, taskid, pcplist, caselog));
		}
		// ���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
		int i = 0;
		while (THREAD_COUNT != 0) {
			i++;
			if (i > 600) {
				break;
			}
			Thread.sleep(6000);
		}
		LogUtil.APP.info("�ף�û����һ�������ҷ�����������Ѿ�ȫ��ִ����ϣ���ȥ������û��ʧ�ܵ������ɣ�");
		threadExecute.shutdown();
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             �ƻ�����ģʽ���ȼƻ�ִ������
	 */

	public static void taskExecutionPlan(TaskExecute task) throws Exception {
		DbLink.exetype = 0;
		String taskid = task.getTaskId().toString();
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(task.getTaskId());
		String jobname = taskScheduling.getSchedulingName();
		int timeout = taskScheduling.getTaskTimeout();
		int[] tastcount = null;
		List<ProjectCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(taskScheduling.getProjectId().toString());
		// ��ʼ��д��������Լ���־ģ��
		LogOperation caselog = new LogOperation();
		// �ж��Ƿ�Ҫ�Զ�����TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// �ж��Ƿ񹹽��Ƿ�ɹ�
			if (buildstatus.indexOf("Status:true") > -1) {
				int threadcount = taskScheduling.getExThreadCount();
				// �����̳߳أ����߳�ִ������
				ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

				List<ProjectCase> cases = GetServerAPI.getCasesbyplanId(taskScheduling.getPlanId());
				LogOperation.updateTaskExecuteStatus(taskid, cases.size());
				int casepriority = 0;
				for (int j = 0; j < cases.size(); j++) {
					ProjectCase projectcase = cases.get(j);
					List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(projectcase.getCaseId());
					if (steps.size() == 0) {
						caselog.insertTaskCaseExecute(taskid, taskScheduling.getProjectId(),projectcase.getCaseId(),projectcase.getCaseSign(), projectcase.getCaseName(), 2);
						LogUtil.APP.warn("������" + projectcase.getCaseSign() + "��û���ҵ����裬ֱ�����������飡");
						caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(), "��������û���ҵ����裬����", "error", "1", "");
						continue;
					}
					// ���̼߳���,����������������ȼ�����������ȼ��ߵ�����ִ����ɣ��ż������������
					if (casepriority < projectcase.getPriority()) {
						LogUtil.APP.info("������ţ�" + projectcase.getCaseSign() + "  casepriority��"
								+ casepriority + "   projectcase.getPriority()��" + projectcase.getPriority());
						LogUtil.APP.info("THREAD_COUNT��" + THREAD_COUNT);
						int i = 0;
						while (THREAD_COUNT != 0) {
							i++;
							if (i > timeout * 60 * 5 / cases.size()) {
								break;
							}
							Thread.sleep(1000);
						}
					}
					casepriority = projectcase.getPriority();
					THREAD_COUNT++; // ���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
					threadExecute.execute(new ThreadForExecuteCase(projectcase, steps, taskid, pcplist, caselog));
				}
				// ���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
				int i = 0;
				while (THREAD_COUNT != 0) {
					i++;
					if (i > timeout * 10) {
						break;
					}
					Thread.sleep(6000);
				}
				tastcount = LogOperation.updateTaskExecuteData(taskid, cases.size());

				String testtime = LogOperation.getTestTime(taskid);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime, jobname),
						taskid, taskScheduling, tastcount);
				threadExecute.shutdown();
				LogUtil.APP.info("�ף�û����һ�������ҷ�����������Ѿ�ȫ��ִ����ϣ���ȥ������û��ʧ�ܵ������ɣ�");
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
