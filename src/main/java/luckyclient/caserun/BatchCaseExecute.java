package luckyclient.caserun;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exappium.androidex.AndroidBatchExecute;
import luckyclient.caserun.exappium.iosex.IosBatchExecute;
import luckyclient.caserun.exinterface.BatchTestCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.ex.WebBatchExecute;
import luckyclient.publicclass.AppiumConfig;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerApi;
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
public class BatchCaseExecute extends TestControl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");
			String taskid = args[0];
			String batchcase = args[1];
			TaskExecute task = GetServerApi.cgetTaskbyid(Integer.valueOf(taskid));
			TaskScheduling taskScheduling = GetServerApi.cGetTaskSchedulingByTaskId(Integer.valueOf(taskid));
			if (taskScheduling.getTaskType() == 0) {
					BatchTestCaseExecution.batchCaseExecuteForTast(taskScheduling.getProject().getProjectName(),
							String.valueOf(task.getTaskId()), batchcase);
			} else if (taskScheduling.getTaskType() == 1) {
					// UI����
					WebBatchExecute.batchCaseExecuteForTast(taskScheduling.getProject().getProjectName(),
							String.valueOf(task.getTaskId()), batchcase);

			} else if (taskScheduling.getTaskType() == 2) {
				Properties properties = AppiumConfig.getConfiguration();

				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidBatchExecute.batchCaseExecuteForTast(taskScheduling.getProject().getProjectName(),
							String.valueOf(task.getTaskId()), batchcase);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosBatchExecute.batchCaseExecuteForTast(taskScheduling.getProject().getProjectName(),
							String.valueOf(task.getTaskId()), batchcase);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("���������������������������쳣�����飡",e);
		} finally{
			System.exit(0);
		}
	}

}
