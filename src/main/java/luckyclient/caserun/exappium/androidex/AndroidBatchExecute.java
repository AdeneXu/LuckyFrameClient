package luckyclient.caserun.exappium.androidex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.caserun.exappium.AppiumInitialization;
import luckyclient.caserun.exappium.AppiumService;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.AppiumConfig;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.TaskExecute;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * 
 * @date 2018��1��26�� ����15:29:40
 * 
 */
public class AndroidBatchExecute {

	public static void batchCaseExecuteForTast(String projectname, String taskid, String batchcase) throws IOException, InterruptedException {
		// ��¼��־�����ݿ�
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		AndroidDriver<AndroidElement> ad = null;
		AppiumService as=null;
		try {
			Properties properties = AppiumConfig.getConfiguration();
			//���������Զ�����Appiume����
			if(Boolean.valueOf(properties.getProperty("autoRunAppiumService"))){
				as =new AppiumService();
				as.start();
				Thread.sleep(10000);
			}
			
			ad = AppiumInitialization.setAndroidAppium(properties);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("��׿�ֻ����������Զ�����Appiume��������쳣",e);
		}
		LogOperation caselog = new LogOperation();
		TaskExecute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		List<ProjectCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getProjectId().toString());
		// ִ��ȫ���ǳɹ�״̬����
		if (batchcase.indexOf("ALLFAIL") > -1) {
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for (int i = 0; i < caseIdList.size(); i++) {
				ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(caseIdList.get(i));
				List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
				// ɾ���ɵ���־
				LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, ad, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				}
			}
		} else { // ����ִ������
			String[] temp = batchcase.split("\\#");
			for (int i = 0; i < temp.length; i++) {
				ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(Integer.valueOf(temp[i]));
				List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
				// ɾ���ɵ���־
				LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, ad, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				}
			}
		}
		LogOperation.updateTaskExecuteData(taskid, 0);
		ad.closeApp();
		//�ر�Appium������߳�
		if(as!=null){
			as.interrupt();
		}
	}

}
