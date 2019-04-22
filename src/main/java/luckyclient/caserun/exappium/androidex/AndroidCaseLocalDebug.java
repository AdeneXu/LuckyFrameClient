package luckyclient.caserun.exappium.androidex;

import java.io.IOException;
import java.util.List;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author seagull
 * @date 2018��1��29��
 * 
 */
public class AndroidCaseLocalDebug {

	public static void oneCasedebug(AndroidDriver<AndroidElement> androiddriver, String testCaseExternalId) {
		// ����¼��־�����ݿ�
		DbLink.exetype = 1;
		LogOperation caselog = new LogOperation();

		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			List<ProjectCaseParams> pcplist = GetServerAPI
					.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
			luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������" + testCaseExternalId + "��......");
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			AndroidCaseExecution.caseExcution(testcase, steps, "888888", androiddriver, caselog, pcplist);

			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������" + testcase.getCaseSign() + "��ִ�����......������һ��");
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
			e.printStackTrace();
		}
	}

	/**
	 * �����������������е���
	 * @param androiddriver
	 * @param projectname
	 * @param addtestcase
	 * @author Seagull
	 * @date 2019��4��18��
	 */
	public static void moreCaseDebug(AndroidDriver<AndroidElement> androiddriver, String projectname,
			List<String> addtestcase) {
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("��ʼ���÷�������Ŀ����" + projectname + "��������ţ�" + testCaseExternalId);
				oneCasedebug(androiddriver, testCaseExternalId);
			} catch (Exception e) {
				continue;
			}
		}
		// �ر�APP�Լ�appium�Ự
		androiddriver.closeApp();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
	}

}
