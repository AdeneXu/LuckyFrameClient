package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
import luckyclient.publicclass.remoterinterface.HttpRequest;
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
 * @ClassName: TestCaseDebug
 * @Description: ����Զ��������ڱ�д�����У��������ű����е��� @author�� seagull
 * @date 2018��3��1��
 * 
 */
public class ApiTestCaseDebug {
	private static final String ASSIGNMENT_SIGN = "$=";
	private static final String FUZZY_MATCHING_SIGN = "%=";
	private static final String REGULAR_MATCHING_SIGN = "~=";

	/**
	 * �����ڱ�����������������
	 * 
	 * @param projectname
	 * @param testCaseExternalId
	 */
	public static void oneCaseDebug(String projectname, String testCaseExternalId) {
		Map<String, String> variable = new HashMap<String, String>(0);
		String packagename = null;
		String functionname = null;
		String expectedresults = null;
		Integer setcaseresult = 0;
		Object[] getParameterValues = null;
		String testnote = "��ʼ�����Խ��";
		int k = 0;
		ProjectCase testcase = GetServerApi.cgetCaseBysign(testCaseExternalId);
		List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
		// �ѹ����������뵽MAP��
		for (ProjectCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsName(), pcp.getParamsValue());
		}
		List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
		if (steps.size() == 0) {
			setcaseresult = 2;
			LogUtil.APP.warn("������δ�ҵ����裬���飡");
			testnote = "������δ�ҵ����裬���飡";
		}
		// ����ѭ���������������в���
		for (int i = 0; i < steps.size(); i++) {
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), "888888",
					null,variable);
			try {
				packagename = casescript.get("PackageName").toString();
				functionname = casescript.get("FunctionName").toString();
			} catch (Exception e) {
				k = 0;
				LogUtil.APP.error("����:{} �����������Ƿ�����ʧ�ܣ����飡",testcase.getCaseSign(),e);
				break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
			}
			// �������ƽ��������쳣���ǵ���������������쳣
			if (functionname.indexOf("�����쳣") > -1 || k == 1) {
				k = 0;
				testnote = "������" + (i + 1) + "��������������";
				break;
			}
			expectedresults = casescript.get("ExpectedResults").toString();
			// �жϷ����Ƿ������
			if (casescript.size() > 4) {
				// ��ȡ�����������������У���ʼ�������������
				getParameterValues = new Object[casescript.size() - 4];
				for (int j = 0; j < casescript.size() - 4; j++) {
					if (casescript.get("FunctionParams" + (j + 1)) == null) {
						k = 1;
						break;
					}
					String parameterValues = casescript.get("FunctionParams" + (j + 1));
					LogUtil.APP.info("����:{} ��������:{} ������:{} ��{}������:{}",testcase.getCaseSign(),packagename,functionname,(j+1),parameterValues);
					getParameterValues[j] = parameterValues;
				}
			} else {
				getParameterValues = null;
			}
			// ���ö�̬������ִ�в�������
			try {
				LogUtil.APP.info("��ʼ���÷���:{} .....",functionname);
				testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
						steps.get(i).getStepType(), steps.get(i).getExtend());
				testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
				if (null != expectedresults && !expectedresults.isEmpty()) {
					LogUtil.APP.info("expectedResults=��{}��",expectedresults);
					// ��ֵ����
					if (expectedresults.length() > ASSIGNMENT_SIGN.length()
							&& expectedresults.startsWith(ASSIGNMENT_SIGN)) {
						variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
						LogUtil.APP
								.info("����:{} ��{}���������Խ����{}����ֵ��������{}��",testcase.getCaseSign(),(i+1),testnote,expectedresults.substring(ASSIGNMENT_SIGN.length()));
					}
					// ģ��ƥ��
					else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length()
							&& expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
						if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
							LogUtil.APP.info(
									"����:{} ��{}����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н��:{}",testcase.getCaseSign(),(i+1),testnote);
						} else {
							setcaseresult = 1;
							LogUtil.APP.warn("����:{} ��{}����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ��{}�����Խ��:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(FUZZY_MATCHING_SIGN.length()),testnote);
							testnote = "������" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
			                if (testcase.getFailcontinue() == 0) {
			                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
			                    break;
			                } else {
			                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
			                }
						}
					}
					// ����ƥ��
					else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length()
							&& expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
						Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
						Matcher matcher = pattern.matcher(testnote);
						if (matcher.find()) {
							LogUtil.APP.info(
									"����:{} ��{}��������ƥ��Ԥ�ڽ���ɹ���ִ�н��:{}",testcase.getCaseSign(),(i+1),testnote);
						} else {
							setcaseresult = 1;
							LogUtil.APP.warn("����:{} ��{}��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ��:{}�����Խ��:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(REGULAR_MATCHING_SIGN.length()),testnote);
							testnote = "������" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
			                if (testcase.getFailcontinue() == 0) {
			                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
			                    break;
			                } else {
			                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
			                }
						}
					}
					// ��ȫ���
					else {
						if (expectedresults.equals(testnote)) {
							LogUtil.APP.info(
									"����:{} ��{}������ȷƥ��Ԥ�ڽ���ɹ���ִ�н��:{}",testcase.getCaseSign(),(i+1),testnote);
						} else {
							setcaseresult = 1;
							LogUtil.APP.warn("����:{} ��{}������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ��:{}�����Խ��:{}",testcase.getCaseSign(),(i+1),expectedresults,testnote);
							testnote = "������" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
			                if (testcase.getFailcontinue() == 0) {
			                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
			                    break;
			                } else {
			                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
			                }
						}
					}
				}
			} catch (Exception e) {
				setcaseresult = 1;
				LogUtil.APP.error("���÷������̳���������:{} �����¼��ű����������Լ�������",functionname,e);
				testnote = "CallCase���ó���";
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......",testcase.getCaseSign(),(i+1));
                    break;
                } else {
                    LogUtil.APP.warn("������{}���ڡ�{}������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......",testcase.getCaseSign(),(i+1));
                }
			}
		}
		variable.clear(); // ��մ���MAP
		// ������÷���������δ�����������ò��Խ������
		if (testnote.indexOf("CallCase���ó���") <= -1 && testnote.indexOf("������������") <= -1) {
			LogUtil.APP.info("����{}�����ɹ������ɹ����������з�����������鿴ִ�н����",testCaseExternalId);
		} else {
			LogUtil.APP.warn("����{}�������ǵ��ò����еķ�������",testCaseExternalId);
		}
		if (0 == setcaseresult) {
			LogUtil.APP.info("����{}����ȫ��ִ�гɹ���",testCaseExternalId);
		} else {
			LogUtil.APP.warn("����{}��ִ�й�����ʧ�ܣ�������־��",testCaseExternalId);
		}
	}

	/**
	 * �����ڱ����������������е���
	 * 
	 * @param projectname
	 * @param addtestcase
	 */
	public static void moreCaseDebug(String projectname, List<String> addtestcase) {
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				LogUtil.APP
						.info("��ʼ���÷�������Ŀ��:{}���������:{}",projectname,testCaseExternalId);
				oneCaseDebug(projectname, testCaseExternalId);
			} catch (Exception e) {
				LogUtil.APP.error("����Debug���������쳣��",e);
				continue;
			}
		}
	}

	/**
	 * ����ϵͳ������ָ�������Ԥ�ڽ��
	 */
	public static String setExpectedResults(String testCaseSign, int steps, String expectedResults) {
		String results = "���ý��ʧ��";
		String params = "";
		try {
			expectedResults = expectedResults.replace("%", "BBFFHH");
			expectedResults = expectedResults.replace("=", "DHDHDH");
			expectedResults = expectedResults.replace("&", "ANDAND");
			params = "caseno=" + testCaseSign;
			params += "&stepnum=" + steps;
			params += "&expectedresults=" + expectedResults;
			results = HttpRequest.sendPost("/projectCasesteps/cUpdateStepExpectedResults.do", params);
		} catch (Exception e) {
			LogUtil.APP.error("����ϵͳ������ָ�������Ԥ�ڽ�������쳣��",e);
			return "����ϵͳ������ָ�������Ԥ�ڽ�������쳣��";
		}
		return results;

	}

	public static void main(String[] args) throws Exception {

	}
}
