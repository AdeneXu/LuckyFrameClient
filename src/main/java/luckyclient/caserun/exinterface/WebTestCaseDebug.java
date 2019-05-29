package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.api.PostServerAPI;
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
 * @ClassName: WebTestCaseDebug
 * @Description: �ṩWeb�˵��Խӿ�
 * @author�� seagull
 * @date 2018��3��1��
 */
public class WebTestCaseDebug {
	private static final String ASSIGNMENT_SIGN = "$=";
	private static final String FUZZY_MATCHING_SIGN = "%=";
	private static final String REGULAR_MATCHING_SIGN = "~=";
	
    /**
     * @param executor
     * @param sign ������WEBҳ���ϵ�������ʱ�ṩ�Ľӿ�
     */
    public static void oneCaseDebug(String caseIdStr, String userIdStr) {
        Map<String, String> variable = new HashMap<>(0);
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "��ʼ�����Խ��";
        int k = 0;
        Integer caseId = Integer.valueOf(caseIdStr);
        Integer userId = Integer.valueOf(userIdStr);
        ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(caseId);

        String sign = testcase.getCaseSign();
        List<ProjectCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
        // �ѹ����������뵽MAP��
        for (ProjectCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
        //����ѭ���������������в���
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), "888888", null);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "��·��");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "������");
            } catch (Exception e) {
                k = 0;
                PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "�����������Ƿ�����ʧ�ܣ����飡",2);
                e.printStackTrace();
                break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
            }
            //�������ƽ��������쳣���ǵ���������������쳣
            if ((null != functionname && functionname.contains("�����쳣")) || k == 1) {
                k = 0;
                testnote = "������" + (i + 1) + "��������������";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, variable, "Ԥ�ڽ��");
            //�жϷ����Ƿ������
            if (casescript.size() > 4) {
                //��ȡ������������������
                getParameterValues = new Object[casescript.size() - 4];
                for (int j = 0; j < casescript.size() - 4; j++) {
                    if (casescript.get("FunctionParams" + (j + 1)) == null) {
                        k = 1;
                        break;
                    }

                    String parameterValues = casescript.get("FunctionParams" + (j + 1));
                    parameterValues = ChangString.changparams(parameterValues, variable, "��������");
                    PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, 0);
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            //���ö�̬������ִ�в�������
            try {
                PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "��ʼ���÷�����" + functionname + " .....",0);

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    // ��ֵ����
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "�����Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��",0);
                    }
                    // ģ��ƥ��
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote,0);
                            testnote = "������" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                                break;
                            } else {
                                LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                            }
                        }
                    }
                    // ����ƥ��
                    else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "����ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote,0);
                            testnote = "������" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                                break;
                            } else {
                                LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                            }
                        }
                    }
                    // ��ȫ���
                    else {
                        if (expectedresults.equals(testnote)) {
                            PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "��ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote,0);
                            testnote = "������" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                                break;
                            } else {
                                LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                setcaseresult = 1;
                PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������",0);
                testnote = "CallCase���ó���";
                e.printStackTrace();
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                    break;
                } else {
                    LogUtil.APP.warn("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                }
            }
        }
        variable.clear();               //��մ���MAP
        //������÷���������δ�����������ò��Խ������
        if (testnote.contains("CallCase���ó���") && testnote.contains("������������")) {
            PostServerAPI.cPostDebugLog(userId, caseId, "ERRORover", "���� " + sign + "�������ǵ��ò����еķ�������",1);
        }
        if (0 == setcaseresult) {
            PostServerAPI.cPostDebugLog(userId, caseId, "INFOover", "���� " + sign + "����ȫ��ִ����ɣ�",1);
        } else {
            PostServerAPI.cPostDebugLog(userId, caseId, "ERRORover", "���� " + sign + "��ִ�й�����ʧ�ܣ����飡",1);
        }
    }

    public static void main(String[] args) throws Exception {
    	
    }
}
