package luckyclient.execution.appium.iosex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.execution.appium.AppiumInitialization;
import luckyclient.execution.appium.AppiumService;
import luckyclient.execution.httpinterface.TestControl;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseParams;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.remote.entity.TaskExecute;
import luckyclient.utils.LogUtil;
import luckyclient.utils.config.AppiumConfig;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * ================================================================= 
 * @author�� seagull 
 * @date 2018��2��2��
 * 
 */
public class IosBatchExecute {

	public static void batchCaseExecuteForTast(String projectname, String taskid, String batchcase) throws IOException, InterruptedException {
		// ��¼��־�����ݿ�
		serverOperation.exetype = 0;
		TestControl.TASKID = taskid;
		IOSDriver<IOSElement> iosd = null;
		AppiumService as=null;
		try {
			Properties properties = AppiumConfig.getConfiguration();
			//���������Զ�����Appiume����
			if(Boolean.valueOf(properties.getProperty("autoRunAppiumService"))){
				as =new AppiumService();
				as.start();
				Thread.sleep(10000);
			}
			
			iosd = AppiumInitialization.setIosAppium(properties);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("���������Զ�����Appiume�������׳��쳣��", e);
		}
		serverOperation caselog = new serverOperation();
		TaskExecute task = GetServerApi.cgetTaskbyid(Integer.valueOf(taskid));
		List<ProjectCaseParams> pcplist = GetServerApi
				.cgetParamsByProjectid(task.getProjectId().toString());
		// ִ��ȫ���ǳɹ�״̬����
		if (batchcase.indexOf("ALLFAIL") > -1) {
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for (int i = 0; i < caseIdList.size(); i++) {
				ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseIdList.get(i));
				List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
				// ɾ���ɵ���־
				serverOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				}
			}
		} else { // ����ִ������
			String[] temp = batchcase.split("\\#");
			for (int i = 0; i < temp.length; i++) {
				ProjectCase testcase = GetServerApi.cGetCaseByCaseId(Integer.valueOf(temp[i]));
				List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
				// ɾ���ɵ���־
				serverOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				}
			}
		}
		serverOperation.updateTaskExecuteData(taskid, 0,2);
		iosd.closeApp();
		//�ر�Appium������߳�
		if(as!=null){
			as.interrupt();
		}
	}

}
