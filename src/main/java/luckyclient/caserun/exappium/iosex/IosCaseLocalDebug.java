package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
import java.util.List;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
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
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author seagull
 * @date 2018��1��29��
 * 
 */
public class IosCaseLocalDebug {

	public static void oneCasedebug(IOSDriver<IOSElement> iosdriver, String testCaseExternalId) {
		// ����¼��־�����ݿ�
		DbLink.exetype = 1;
		LogOperation caselog = new LogOperation();

		try {
			ProjectCase testcase = GetServerApi.cgetCaseBysign(testCaseExternalId);
			List<ProjectCaseParams> pcplist = GetServerApi
					.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
			LogUtil.APP.info("��ʼִ����������{}��......",testCaseExternalId);
			List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
			IosCaseExecution.caseExcution(testcase, steps, "888888", iosdriver, caselog, pcplist);

			LogUtil.APP.info("��ǰ��������{}��ִ�����......������һ��",testcase.getCaseSign());
		} catch (Exception e) {
			LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
		}
	}

	/**
	 * @param ��Ŀ��
	 * @param �������
	 * @param �����汾��
	 *            ������testlink�����ú������������������������е���
	 */
	public static void moreCaseDebug(IOSDriver<IOSElement> iosdriver, String projectname,
			List<String> addtestcase) {
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				LogUtil.APP
						.info("��ʼ���÷�������Ŀ����{}��������ţ�{}",projectname,testCaseExternalId);
				oneCasedebug(iosdriver, testCaseExternalId);
			} catch (Exception e) {
				LogUtil.APP.error("���������Թ������׳��쳣��", e);
				continue;
			}
		}
		// �ر�APP�Լ�appium�Ự
		iosdriver.closeApp();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
	}

}
