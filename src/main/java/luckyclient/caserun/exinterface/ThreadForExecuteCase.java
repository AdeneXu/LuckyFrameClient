package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
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
 * @ClassName: ThreadForExecuteCase
 * @Description: �̳߳ط�ʽִ������
 * @author�� seagull
 * @date 2018��3��1��
 */
public class ThreadForExecuteCase extends Thread {
    private static final String ASSIGNMENT_SIGN = "$=";
    private static final String ASSIGNMENT_GLOBALSIGN = "$A=";
    private static final String FUZZY_MATCHING_SIGN = "%=";
    private static final String REGULAR_MATCHING_SIGN = "~=";

    private Integer caseId;
    private String caseSign;
    private ProjectCase testcase;
    private String taskid;
    private Integer projectId;
    private List<ProjectCaseSteps> steps;
    private List<ProjectCaseParams> pcplist;
    private LogOperation caselog;

    public ThreadForExecuteCase(ProjectCase projectcase, List<ProjectCaseSteps> steps, String taskid, List<ProjectCaseParams> pcplist, LogOperation caselog) {
        this.caseId = projectcase.getCaseId();
        this.testcase = projectcase;
        this.projectId = projectcase.getProjectId();
        this.caseSign = projectcase.getCaseSign();
        this.taskid = taskid;
        this.steps = steps;
        this.pcplist = pcplist;
        this.caselog = caselog;
    }

    @Override
    public void run() {
        Map<String, String> variable = new HashMap<>(0);
        // �ѹ����������뵽MAP��
        for (ProjectCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        // ����ȫ�ֱ���
        variable.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        String functionname = null;
        String packagename = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "��ʼ�����Խ��";
        int k = 0;
        // ����ѭ�������������������в���
        // ���뿪ʼִ�е�����
        caselog.insertTaskCaseExecute(taskid, projectId, caseId, caseSign, testcase.getCaseName(), 3);
        for (int i = 0; i < steps.size(); i++) {
            // �������������еĽű�
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), taskid, caselog);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "��·��");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "������");
            } catch (Exception e) {
                k = 0;
                LogUtil.APP.error("������" + testcase.getCaseSign() + "�����������Ƿ�����ʧ�ܣ����飡");
                caselog.insertTaskCaseLog(taskid, caseId, "�����������Ƿ�����ʧ�ܣ����飡", "error", String.valueOf(i + 1), "");
                e.printStackTrace();
                break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
            }
            // �������ƽ��������쳣���ǵ���������������쳣
            if ((null != functionname && functionname.contains("�����쳣")) || k == 1) {
                k = 0;
                testnote = "������" + (i + 1) + "��������������";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, variable, "Ԥ�ڽ��");
            // �жϷ����Ƿ������
            if (casescript.size() > 4) {
                // ��ȡ������������������
                getParameterValues = new Object[casescript.size() - 4];
                for (int j = 0; j < casescript.size() - 4; j++) {
                    if (casescript.get("FunctionParams" + (j + 1)) == null) {
                        k = 1;
                        break;
                    }
                    String parameterValues = casescript.get("FunctionParams" + (j + 1));
                    parameterValues = ChangString.changparams(parameterValues, variable, "��������");
                    LogUtil.APP.info("������" + testcase.getCaseSign() + "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues);
                    caselog.insertTaskCaseLog(taskid, caseId, "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // ���ö�̬������ִ�в�������
            try {
                LogUtil.APP.info("������" + testcase.getCaseSign() + "��ʼ���÷�����" + functionname + " .....");
                caselog.insertTaskCaseLog(taskid, caseId, "��ʼ���÷�����" + functionname + " .....", "info", String.valueOf(i + 1), "");

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    LogUtil.APP.info("expectedResults=��" + expectedresults + "��");
                    // ��ֵ����
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + (i + 1) + "���������Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��");
                        caselog.insertTaskCaseLog(taskid, caseId, "�����Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��", "info", String.valueOf(i + 1), "");
                    }
                    // ��ֵȫ�ֱ���
                    else if (expectedresults.length() > ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + (i + 1) + "���������Խ����" + testnote + "����ֵ��ȫ�ֱ�����" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��");
                        caselog.insertTaskCaseLog(taskid, caseId, "�����Խ����" + testnote + "����ֵ��ȫ�ֱ�����" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��", "info", String.valueOf(i + 1), "");
                    }
                    // ģ��ƥ��
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("������" + testcase.getCaseSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote, "error", String.valueOf(i + 1), "");
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
                            LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "����ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("������" + testcase.getCaseSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote, "error", String.valueOf(i + 1), "");
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
                            LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "��ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("������" + testcase.getCaseSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote, "error", String.valueOf(i + 1), "");
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
                LogUtil.APP.error("������" + testcase.getCaseSign() + "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������",e);
                caselog.insertTaskCaseLog(taskid, caseId, "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������", "error", String.valueOf(i + 1), "");
                testnote = "CallCase���ó������÷������̳�����������" + functionname + " �����¼��ű����������Լ�������";
                setcaseresult = 1;
                e.printStackTrace();
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.error("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                    break;
                } else {
                    LogUtil.APP.error("������"+testcase.getCaseSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                }
            }
        }
        // ������÷���������δ�����������ò��Խ������
        try {
            // �ɹ���ʧ�ܵ������ߴ�����
            if (!testnote.contains("CallCase���ó���") && !testnote.contains("������������")) {
                caselog.updateTaskCaseExecuteStatus(taskid, caseId, setcaseresult);
            } else {
                // �����������ǵ��÷�������ȫ����������Ϊ����
                LogUtil.APP.warn("������" + testcase.getCaseSign() + "����ִ�н��Ϊ��������ο�������־��������������ԭ��.....");
                caselog.insertTaskCaseLog(taskid, caseId, "����ִ�н��Ϊ��������ο�������־��������������ԭ��.....","error", "SETCASERESULT...", "");
                setcaseresult = 2;
                caselog.updateTaskCaseExecuteStatus(taskid, caseId, setcaseresult);
            }
            if (0 == setcaseresult) {
                LogUtil.APP.info("������" + testcase.getCaseSign() + "ִ�н���ɹ�......");
                caselog.insertTaskCaseLog(taskid, caseId, "��������ִ��ȫ���ɹ�......", "info", "ending", "");
                LogUtil.APP.info("*********������" + testcase.getCaseSign() + "��ִ�����,���Խ�����ɹ�*********");
            } else if (1 == setcaseresult) {
                LogUtil.APP.warn("������" + testcase.getCaseSign() + "ִ�н��ʧ��......");
                caselog.insertTaskCaseLog(taskid, caseId, "����ִ�н��ʧ��......", "error", "ending", "");
                LogUtil.APP.info("*********������" + testcase.getCaseSign() + "��ִ�����,���Խ����ʧ��*********");
            } else {
                LogUtil.APP.warn("������" + testcase.getCaseSign() + "ִ�н������......");
                caselog.insertTaskCaseLog(taskid, caseId, "����ִ�н������......", "error", "ending", "");
                LogUtil.APP.info("*********������" + testcase.getCaseSign() + "��ִ�����,���Խ��������*********");
            }
        } catch (Exception e) {
            LogUtil.APP.error("������" + testcase.getCaseSign() + "����ִ�н�����̳���......",e);
            caselog.insertTaskCaseLog(taskid, caseId, "����ִ�н�����̳���......", "error", "ending", "");
        } finally {
            variable.clear(); // һ��������������ձ����洢�ռ�
            TestControl.THREAD_COUNT--; // ���̼߳���--�����ڼ���߳��Ƿ�ȫ��ִ����
        }
    }

    public static void main(String[] args) {
    }

}
