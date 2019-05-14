package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.AppDriverAnalyticCase;
import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.LogOperation;
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
 * @author seagull
 * @date 2018��1��21�� ����15:12:48
 */
public class IosCaseExecution extends TestCaseExecution{
	static Map<String, String> variable = new HashMap<String, String>();
    private static String casenote = "��ע��ʼ��";
    private static String imagname = "";

	public static void caseExcution(ProjectCase testcase, List<ProjectCaseSteps> steps,String taskid, IOSDriver<IOSElement> appium,LogOperation caselog,List<ProjectCaseParams> pcplist)
			throws InterruptedException, IOException {
		caselog.updateTaskCaseExecuteStatus(taskid, testcase.getCaseId(), 3);
		// �ѹ����������뵽MAP��
		for (ProjectCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsName(), pcp.getParamsValue());
		}
		// ����ȫ�ֱ���
        variable.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        
	    // 0:�ɹ� 1:ʧ�� 2:���� ����������
	    int setcaseresult = 0;
		for (ProjectCaseSteps step : steps) {
            Map<String, String> params;
            String result;
			
            // ���ݲ��������������������
            if (3 == step.getStepType()){
            	params = AppDriverAnalyticCase.analyticCaseStep(testcase, step, taskid,caselog);
            }else{
            	params = InterfaceAnalyticCase.analyticCaseStep(testcase, step, taskid, caselog);
            }
            
			if(params.get("exception")!=null&&params.get("exception").toString().indexOf("�����쳣")>-1){
				setcaseresult = 2;
				break;
			}
			
            // ���ݲ���������ִ�в���
            if (3 == step.getStepType()){
            	result = iosRunStep(params, variable, appium, taskid, testcase.getCaseId(), step.getStepSerialNumber(), caselog);
            }else{
            	result = TestCaseExecution.runStep(params, variable, taskid, testcase.getCaseSign(), step, caselog);
            }

			String expectedResults = params.get("ExpectedResults").toString();
			expectedResults=ChangString.changparams(expectedResults, variable,"Ԥ�ڽ��");

            // �жϽ��
			int stepresult = judgeResult(testcase, step, params, appium, taskid, expectedResults, result, caselog);
			// ʧ�ܣ����Ҳ��ڼ���,ֱ����ֹ
            if (0 != stepresult) {
            	setcaseresult = stepresult;
                if (testcase.getFailcontinue() == 0) {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcase.getCaseSign()+"���ڡ�"+step.getStepSerialNumber()+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                    break;
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcase.getCaseSign()+"���ڡ�"+step.getStepSerialNumber()+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                }
            }

		}

		variable.clear();
		caselog.updateTaskCaseExecuteStatus(taskid, testcase.getCaseId(), setcaseresult);
		if(setcaseresult==0){
			luckyclient.publicclass.LogUtil.APP.info("������"+testcase.getCaseSign()+"��ȫ������ִ�н���ɹ�...");
	        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "����ȫ������ִ�н���ɹ�","info", "ending","");
		}else{
			luckyclient.publicclass.LogUtil.APP.error("������"+testcase.getCaseSign()+"������ִ�й�����ʧ�ܻ�������...��鿴����ԭ��"+casenote);
	        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "����ִ�й�����ʧ�ܻ�������"+casenote,"error", "ending","");
		}
		//LogOperation.UpdateTastdetail(taskid, 0);
	}

	public static String iosRunStep(Map<String, String> params, Map<String, String> variable, IOSDriver<IOSElement> appium,String taskid,Integer caseId,int stepno,LogOperation caselog) {
		String result = "";
		String property;
		String propertyValue;
		String operation;
		String operationValue;

		try {
			property = params.get("property");
			propertyValue = params.get("property_value");
			operation = params.get("operation");
			operationValue = params.get("operation_value");

			// ����ֵ����
			property = ChangString.changparams(property, variable,"��λ��ʽ");
			propertyValue=ChangString.changparams(propertyValue, variable,"��λ·��");
			operation=ChangString.changparams(operation, variable,"����");
			operationValue=ChangString.changparams(operationValue, variable,"��������");
			
			luckyclient.publicclass.LogUtil.APP.info("���ν�������������ɣ��ȴ����ж������......");
			caselog.insertTaskCaseLog(taskid, caseId, "�������:"+operation+"; ����ֵ:"+operationValue,"info", String.valueOf(stepno),"");
		} catch (Exception e) {
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("���ν������������׳��쳣��---"+e.getMessage());
			return "����ִ��ʧ�ܣ���������ʧ��!";
		}

		try {		
			//���ýӿ�����
			if(null != operation&&null != operationValue&&"runcase".equals(operation)){
				String[] temp=operationValue.split(",",-1);
				String ex = TestCaseExecution.oneCaseExecuteForUICase(temp[0], taskid, caselog, appium);
				if(ex.indexOf("CallCase���ó���")<=-1&&ex.indexOf("������������")<=-1&&ex.indexOf("ƥ��ʧ��")<=-1){
					return ex;
				}else{
					return "����ִ��ʧ�ܣ����ýӿ���������ʧ��";
				}
			}
			
			IOSElement ae = null;
			// ҳ��Ԫ�ز�
			if (null != property && null != propertyValue) { 
				ae = isElementExist(appium, property, propertyValue);
				// �жϴ�Ԫ���Ƿ����
				if (null==ae) {
					luckyclient.publicclass.LogUtil.APP.error("��λ����ʧ�ܣ�isElementExistΪnull!");
					return "����ִ��ʧ�ܣ�isElementExist��λԪ�ع���ʧ�ܣ�";
				}

				if (operation.indexOf("select") > -1) {
					result = IosEncapsulateOperation.selectOperation(ae, operation, operationValue);
				} else if (operation.indexOf("get") > -1){
					result = IosEncapsulateOperation.getOperation(ae, operation,operationValue);
				} else {
					result = IosEncapsulateOperation.objectOperation(appium, ae, operation, operationValue, property, propertyValue);
				}
				// Driver�����
			} else if (null==property && null != operation) { 				
				// ���������¼�
				if (operation.indexOf("alert") > -1){
					result = IosEncapsulateOperation.alertOperation(appium, operation);
				}else{
					result = IosEncapsulateOperation.driverOperation(appium, operation, operationValue);
				} 				
			}else{
				luckyclient.publicclass.LogUtil.APP.error("Ԫ�ز�������ʧ�ܣ�");
				result =  "����ִ��ʧ�ܣ�Ԫ�ز�������ʧ�ܣ�";
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("Ԫ�ض�λ���̻��ǲ�������ʧ�ܻ��쳣��"+e.getMessage());
			return "����ִ��ʧ�ܣ�Ԫ�ض�λ���̻��ǲ�������ʧ�ܻ��쳣��" + e.getMessage();
		}
		caselog.insertTaskCaseLog(taskid, caseId, result,"info", String.valueOf(stepno),"");
		
		if(result.indexOf("��ȡ����ֵ�ǡ�")>-1&&result.indexOf("��")>-1){
			result = result.substring(result.indexOf("��ȡ����ֵ�ǡ�")+7, result.length()-1);
		}
		return result;

	}

	public static IOSElement isElementExist(IOSDriver<IOSElement> appium, String property, String propertyValue) {
		try {
			IOSElement ae = null;
			property=property.toLowerCase();
			// ����WebElement����λ
			switch (property) {
			case "id":
				ae = appium.findElementById(propertyValue);
				break;
			case "name":
				ae = appium.findElementByName(propertyValue);
				break;
			case "xpath":
				ae = appium.findElementByXPath(propertyValue);
				break;
			case "linktext":
				ae = appium.findElementByLinkText(propertyValue);
				break;
			case "tagname":
				ae = appium.findElementByTagName(propertyValue);
				break;
			case "cssselector":
				ae = appium.findElementByCssSelector(propertyValue);
				break;
			case "classname":
				ae = appium.findElementByClassName(propertyValue);
				break;
			case "accessibilityid":
				ae = appium.findElementByAccessibilityId(propertyValue);
				break;
			case "iosclasschain":
				ae = appium.findElementByIosClassChain(propertyValue);
				break;
			case "iosnspredicate":
				ae = appium.findElementByIosNsPredicate(propertyValue);
				break;
			case "iosuiautomation":
				ae = appium.findElementByIosUIAutomation(propertyValue);
				break;
			default:
				break;
			}

			return ae;

		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("��ǰ����λʧ�ܣ�"+e.getMessage());
			return null;
		}
		
	}

	public static int judgeResult(ProjectCase testcase, ProjectCaseSteps step, Map<String, String> params, IOSDriver<IOSElement> appium, String taskid, String expect, String result, LogOperation caselog) throws InterruptedException {
        int setresult = 0;
        java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
        imagname = timeformat.format(new Date());
        
        result = ActionManageForSteps.actionManage(step.getAction(), result);
        if (null != result && !result.contains("����ִ��ʧ�ܣ�")) {
            // ��Ԥ�ڽ��
            if (null != expect && !expect.isEmpty()) {
                luckyclient.publicclass.LogUtil.APP.info("�������Ϊ��" + expect + "��");

                // ��ֵ����ģʽ
                if (expect.length() > ASSIGNMENT_SIGN.length() && expect.startsWith(ASSIGNMENT_SIGN)) {
                    variable.put(expect.substring(ASSIGNMENT_SIGN.length()), result);
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "���������Խ����" + result + "����ֵ��������" + expect.substring(ASSIGNMENT_SIGN.length()) + "��");
                    caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "�����Խ����" + result + "����ֵ��������" + expect.substring(ASSIGNMENT_SIGN.length()) + "��", "info", String.valueOf(step.getStepSerialNumber()), "");
                }
                // ��ֵȫ�ֱ���
                else if (expect.length() > ASSIGNMENT_GLOBALSIGN.length() && expect.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                	variable.put(expect.substring(ASSIGNMENT_GLOBALSIGN.length()), result);
                	ParamsManageForSteps.GLOBAL_VARIABLE.put(expect.substring(ASSIGNMENT_GLOBALSIGN.length()), result);
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "���������Խ����" + result + "����ֵ��ȫ�ֱ�����" + expect.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��");
                    caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "�����Խ����" + result + "����ֵ��ȫ�ֱ�����" + expect.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��", "info", String.valueOf(step.getStepSerialNumber()), "");
                }
                // �ƶ��� UI���ģʽ
                else if (3 == step.getStepType() && params.get("checkproperty") != null && params.get("checkproperty_value") != null) {
                    String checkproperty = params.get("checkproperty");
                    String checkPropertyValue = params.get("checkproperty_value");

                    IOSElement ae = isElementExist(appium, checkproperty, checkPropertyValue);
                    if (null != ae) {
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "�����ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ�гɹ���");
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "�ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ�гɹ���", "info", String.valueOf(step.getStepSerialNumber()), "");
                    } else {
                        casenote = "��" + step.getStepSerialNumber() + "����û���ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж���ִ��ʧ�ܣ�";
                        setresult = 1;
                        IosBaseAppium.screenShot(appium, imagname);
                        luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "����û���ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ��ʧ�ܣ�");
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "�ڵ�ǰҳ����û���ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ��ʧ�ܣ�" + "checkproperty��" + checkproperty + "��  checkproperty_value��" + checkPropertyValue + "��", "error", String.valueOf(step.getStepSerialNumber()), imagname);
                    }
                }
                // ����ƥ��ģʽ
                else {
                    // ģ��ƥ��Ԥ�ڽ��ģʽ
                    if (expect.length() > FUZZY_MATCHING_SIGN.length() && expect.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (result.contains(expect.substring(FUZZY_MATCHING_SIGN.length()))) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + result);
                            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + result, "info", String.valueOf(step.getStepSerialNumber()), "");
                        } else {
                            casenote = "��" + step.getStepSerialNumber() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            setresult = 1;
                            IosBaseAppium.screenShot(appium, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + result);
                            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + result, "error", String.valueOf(step.getStepSerialNumber()), imagname);
                        }
                    }
                    // ����ƥ��Ԥ�ڽ��ģʽ
                    else if (expect.length() > REGULAR_MATCHING_SIGN.length() && expect.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expect.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(result);
                        if (matcher.find()) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + result);
                            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "����ƥ��Ԥ�ڽ���ɹ���", "info", String.valueOf(step.getStepSerialNumber()), "");
                        } else {
                            casenote = "��" + step.getStepSerialNumber() + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            setresult = 1;
                            IosBaseAppium.screenShot(appium, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + result);
                            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "����ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + result, "error", String.valueOf(step.getStepSerialNumber()), imagname);
                        }
                    }
                    // ��ȷƥ��Ԥ�ڽ��ģʽ
                    else {
                        if (expect.equals(result)) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + result);
                            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "��ȷƥ��Ԥ�ڽ���ɹ���", "info", String.valueOf(step.getStepSerialNumber()), "");
                        } else {
                            casenote = "��" + step.getStepSerialNumber() + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                            setresult = 1;
                            IosBaseAppium.screenShot(appium, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ���ǣ���"+expect+"��  ִ�н������"+ result+"��");
                            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "��ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ���ǣ���"+expect+"��  ִ�н������"+ result+"��", "error", String.valueOf(step.getStepSerialNumber()), imagname);
                        }
                    }
                }
            }
        } else {
            casenote = (null != result) ? result : "";
            setresult = 2;
            IosBaseAppium.screenShot(appium, imagname);
            LogUtil.APP.error("������" + testcase.getCaseSign() + " ��" + step.getStepSerialNumber() + "����ִ�н����" + casenote);
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "��ǰ������ִ�й����н���|��λԪ��|��������ʧ�ܣ�" + casenote, "error", String.valueOf(step.getStepSerialNumber()), imagname);
        }
        
        return setresult;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
