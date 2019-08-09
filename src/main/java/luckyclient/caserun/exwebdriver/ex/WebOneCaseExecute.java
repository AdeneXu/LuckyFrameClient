package luckyclient.caserun.exwebdriver.ex;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebDriverInitialization;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerApi;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;

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
public class WebOneCaseExecute{
	
	public static void oneCaseExecuteForTast(String projectname,Integer caseId,int version,String taskid){
		//��¼��־�����ݿ�
		DbLink.exetype = 0;   
		TestControl.TASKID = taskid;
		int drivertype = LogOperation.querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(drivertype);
		} catch (IOException e1) {
			LogUtil.APP.error("��ʼ��WebDriver����", e1);
		}
		LogOperation caselog = new LogOperation();
		ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseId);
		//ɾ���ɵ���־
		LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);    

		List<ProjectCaseParams> pcplist=GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
		LogUtil.APP.info("��ʼִ������:��{}��......",testcase.getCaseSign());
		try {
			List<ProjectCaseSteps> steps=GetServerApi.getStepsbycaseid(testcase.getCaseId());
			WebCaseExecution.caseExcution(testcase, steps, taskid,wd,caselog,pcplist);
			LogUtil.APP.info("��ǰ����:��{}��ִ�����......������һ��",testcase.getCaseSign());
		} catch (InterruptedException e) {
			LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
		}
		LogOperation.updateTaskExecuteData(taskid, 0,2);
        //�ر������
        wd.quit();
	}

}
