package luckyclient.caserun.exappium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.androidex.AndroidCaseExecution;
import luckyclient.caserun.exappium.iosex.IosCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.jenkinsapi.BuildingInitialization;
import luckyclient.jenkinsapi.RestartServerInitialization;
import luckyclient.mail.HtmlMail;
import luckyclient.mail.MailSendInitialization;
import luckyclient.publicclass.AppiumConfig;
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
public class AppTestControl {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             ����̨ģʽ���ȼƻ�ִ������
	 */

	public static void manualExecutionPlan(String planname) {
		// ������־�����ݿ�
		DbLink.exetype = 1;
		String taskid = "888888";
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = AppiumConfig.getConfiguration();
		try {
			if ("Android".equals(properties.getProperty("platformName"))) {
				androiddriver = AppiumInitialization.setAndroidAppium(properties);
			} else if ("IOS".equals(properties.getProperty("platformName"))) {
				iosdriver = AppiumInitialization.setIosAppium(properties);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("����̨ģʽ��ʼ��Appium Driver�쳣��", e);
		}
		LogOperation caselog = new LogOperation();
		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<ProjectCaseParams>();
		if (testCases.size() != 0) {
			pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}
		LogUtil.APP.info("��ǰ�ƻ��ж�ȡ��������{}��",testCases.size());
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			LogUtil.APP.info("��ʼִ�мƻ��еĵ�{}����������{}��......",i,testcase.getCaseSign());
			try {
				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, androiddriver, caselog, pcplist);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosdriver, caselog, pcplist);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogUtil.APP.error("�û�ִ�й������׳�InterruptedException�쳣��", e);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogUtil.APP.error("�û�ִ�й������׳�IOException�쳣��", e);
			}
			LogUtil.APP.info("��ǰ��������{}��ִ�����......������һ��",testcase.getCaseSign());
		}
		LogUtil.APP.info("��ǰ��Ŀ���Լƻ��е������Ѿ�ȫ��ִ�����...");
		// �ر�APP�Լ�appium�Ự
		if ("Android".equals(properties.getProperty("platformName"))) {
			androiddriver.closeApp();
		} else if ("IOS".equals(properties.getProperty("platformName"))) {
			iosdriver.closeApp();
		}
	}

	public static void taskExecutionPlan(TaskExecute task) throws InterruptedException {
		// ��¼��־�����ݿ�
		String taskId=task.getTaskId().toString();
		DbLink.exetype = 0;
		TestControl.TASKID = taskId;
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = AppiumConfig.getConfiguration();
		AppiumService as=null;
		//���������Զ�����Appiume����
		if(Boolean.valueOf(properties.getProperty("autoRunAppiumService"))){
			as =new AppiumService();
			as.start();
			Thread.sleep(10000);
		}
		TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(task.getTaskId());
		String restartstatus = RestartServerInitialization.restartServerRun(taskId);
		String buildstatus = BuildingInitialization.buildingRun(taskId);
		List<ProjectCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getProjectId().toString());
		String projectname = task.getProject().getProjectName();
		String jobname = GetServerAPI.cGetTaskSchedulingByTaskId(task.getTaskId()).getSchedulingName();
        int[] tastcount = null;
		// �ж��Ƿ�Ҫ�Զ�����TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// �ж��Ƿ񹹽��Ƿ�ɹ�
			if (buildstatus.indexOf("Status:true") > -1) {
				try {
					if ("Android".equals(properties.getProperty("platformName"))) {
						androiddriver = AppiumInitialization.setAndroidAppium(properties);
						LogUtil.APP.info("���AndroidDriver��ʼ������...APPIUM Server��http://{}/wd/hub��",properties.getProperty("appiumsever"));
					} else if ("IOS".equals(properties.getProperty("platformName"))) {
						iosdriver = AppiumInitialization.setIosAppium(properties);
						LogUtil.APP.info("���IOSDriver��ʼ������...APPIUM Server��http://{}/wd/hub��",properties.getProperty("appiumsever"));
					}
				} catch (Exception e) {
					LogUtil.APP.error("��ʼ��AppiumDriver���� ��APPIUM Server��http://{}/wd/hub��",properties.getProperty("appiumsever"), e);
				}
				LogOperation caselog = new LogOperation();
				List<ProjectCase> cases = GetServerAPI.getCasesbyplanId(taskScheduling.getPlanId());
				LogUtil.APP.info("��ǰ�ƻ��ж�ȡ��������{}��",cases.size());
				LogOperation.updateTaskExecuteStatus(taskId, cases.size());

				for (ProjectCase testcase : cases) {
					List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
					if (steps.size() == 0) {
						continue;
					}
					LogUtil.APP.info("��ʼִ����������{}��......",testcase.getCaseSign());
					try {
						//���뿪ʼִ�е�����
						caselog.insertTaskCaseExecute(taskId, taskScheduling.getProjectId(),testcase.getCaseId(),testcase.getCaseSign(), testcase.getCaseName(), 4);
						if ("Android".equals(properties.getProperty("platformName"))) {
							AndroidCaseExecution.caseExcution(testcase, steps, taskId, androiddriver, caselog, pcplist);
						} else if ("IOS".equals(properties.getProperty("platformName"))) {
							IosCaseExecution.caseExcution(testcase, steps, taskId, iosdriver, caselog, pcplist);
						}
					} catch (InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
					}
					LogUtil.APP.info("��ǰ��������{}��ִ�����......������һ��",testcase.getCaseSign());
				}
				tastcount = LogOperation.updateTaskExecuteData(taskId, cases.size());
				String testtime = LogOperation.getTestTime(taskId);
				LogUtil.APP.info("��ǰ��Ŀ��{]�����Լƻ��е������Ѿ�ȫ��ִ�����...",projectname);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskId, buildstatus, restartstatus, testtime, jobname),
						taskId, taskScheduling, tastcount);
				// �ر�APP�Լ�appium�Ự
				if ("Android".equals(properties.getProperty("platformName"))) {
					androiddriver.closeApp();
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					iosdriver.closeApp();
				}
			} else {
				LogUtil.APP.warn("��Ŀ����ʧ�ܣ��Զ��������Զ��˳�����ǰ��JENKINS�м����Ŀ���������");
				MailSendInitialization.sendMailInitialization(jobname, "������Ŀ������ʧ�ܣ��Զ��������Զ��˳�����ǰȥJENKINS�鿴���������", taskId, taskScheduling, tastcount);
			}
		} else {
			LogUtil.APP.warn("��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������");
			MailSendInitialization.sendMailInitialization(jobname, "��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������", taskId, taskScheduling, tastcount);
		}
		//�ر�Appium������߳�
		if(as!=null){
			as.interrupt();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
