package luckyclient.caserun.exwebdriver.ex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebDriverInitialization;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.TaskExecute;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class WebBatchExecute{
	
	public static void batchCaseExecuteForTast(String projectname,String taskid,String batchcase) throws IOException{
		//��¼��־�����ݿ�
		DbLink.exetype = 0;   
		TestControl.TASKID = taskid;
		int drivertype = LogOperation.querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(drivertype);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		TaskExecute task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		List<ProjectCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(task.getProjectId().toString());
		 //ִ��ȫ���ǳɹ�״̬����
		if(batchcase.indexOf("ALLFAIL")>-1){   
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for(int i=0;i<caseIdList.size();i++){
			   ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(caseIdList.get(i));
			   List<ProjectCaseSteps> steps=GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			   //ɾ���ɵ���־
			   LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);    
			   try {
				WebCaseExecution.caseExcution(testcase, steps, taskid,wd,caselog,pcplist);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				e.printStackTrace();
			 }
			}			
		}else{                                           //����ִ������
			String[] temp=batchcase.split("\\#");
			for(int i=0;i<temp.length;i++){
				ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(Integer.valueOf(temp[i]));
				List<ProjectCaseSteps> steps=GetServerAPI.getStepsbycaseid(testcase.getCaseId());
				//ɾ���ɵ���־
				LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					WebCaseExecution.caseExcution(testcase, steps,taskid,wd,caselog,pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
					e.printStackTrace();
				}
			}
		}
		LogOperation.updateTaskExecuteData(taskid, 0);
        //�ر������
        wd.quit();
	}
	
}
