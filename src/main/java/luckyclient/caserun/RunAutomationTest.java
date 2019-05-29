package luckyclient.caserun;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exappium.AppTestControl;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebTestControl;
import luckyclient.serverapi.api.GetServerAPI;
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
public class RunAutomationTest extends TestControl {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");
			String taskid = args[0];
			TaskExecute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(Integer.valueOf(taskid));
			if (taskScheduling.getTaskType() == 0) {
				// �ӿڲ���
				TestControl.taskExecutionPlan(task);
			} else if (taskScheduling.getTaskType() == 1) {
				// UI����
				WebTestControl.taskExecutionPlan(task);
			} else if (taskScheduling.getTaskType() == 2) {
				AppTestControl.taskExecutionPlan(task);
			}
	 		System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
