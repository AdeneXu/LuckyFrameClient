package luckyclient.caserun.exwebdriver;

import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
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
public class CaseLocalDebug{

	
	public static void oneCasedebug(WebDriver wd,String testCaseExternalId){
		 //����¼��־�����ݿ�
		DbLink.exetype = 1;  
		LogOperation caselog = new LogOperation();
		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			List<ProjectCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
			LogUtil.APP.info("��ʼִ������:��{}��......",testCaseExternalId);
			List<ProjectCaseSteps> steps=GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			WebCaseExecution.caseExcution(testcase,steps, "888888",wd,caselog,pcplist);
			LogUtil.APP.info("��ǰ��������{}��ִ�����......������һ��",testcase.getCaseSign());
		} catch (Exception e) {
			LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
		}
        //�ر������
        wd.quit();
	}
	
	/**
	 * @param ��Ŀ��
	 * @param �������
	 * @param �����汾��
	 * ������testlink�����ú������������������������е���
	 */
	public static void moreCaseDebug(WebDriver wd,String projectname,List<String> addtestcase){
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
		    try{
		    LogUtil.APP.info("��ʼ���÷�������Ŀ��:{}���������:{}",projectname,testCaseExternalId); 
		    oneCasedebug(wd,testCaseExternalId);
		    }catch(Exception e){
		    	continue;
		    }
		}
	}

}
