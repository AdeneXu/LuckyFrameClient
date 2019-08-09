package luckyclient.serverapi.api;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.remoterinterface.HttpRequest;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.ProjectProtocolTemplate;
import luckyclient.serverapi.entity.ProjectTemplateParams;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;


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
public class GetServerApi {
	
	private static final String PREFIX = "/openGetApi";
	/**
	 * ͨ���ƻ�ID��ȡ������������
	 * @param planid
	 * @return
	 */
	public static List<ProjectCase> getCasesbyplanId(int planId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListByPlanId.do?planId=" + planId);		
        List<ProjectCase> caseList = JSONObject.parseArray(result, ProjectCase.class);
		return caseList;
	}

	/**
	 * ͨ���ƻ����ƻ�ȡ������������
	 * @param name
	 * @return
	 */
	public static List<ProjectCase> getCasesbyplanname(String name) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListByPlanName.do?planName=" + name);
		List<ProjectCase> caseList = JSONObject.parseArray(result, ProjectCase.class);
		return caseList;
	}

	/**
	 * ͨ������ID��ȡ����Ĳ������
	 * @param caseid
	 * @return
	 */
	public static List<ProjectCaseSteps> getStepsbycaseid(Integer caseid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetStepListByCaseId.do?caseId=" + caseid);
		List<ProjectCaseSteps> stepsList = JSONObject.parseArray(result, ProjectCaseSteps.class);
		return stepsList;
	}

	/**
	 * ͨ��taskid��ȡ����
	 * @param taskid
	 * @return
	 */
	public static TaskExecute cgetTaskbyid(int taskid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetTaskByTaskId.do?taskId=" + taskid);
		TaskExecute task = JSONObject.parseObject(result, TaskExecute.class);
		return task;
	}
	
	/**
	 * ͨ��taskid��ȡ���ȶ���
	 * @param taskid
	 * @return
	 */
	public static TaskScheduling cGetTaskSchedulingByTaskId(int taskid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetTaskSchedulingByTaskId.do?taskId=" + taskid);
		TaskScheduling taskScheduling = JSONObject.parseObject(result, TaskScheduling.class);
		return taskScheduling;
	}

	/**
	 * ͨ��������Ż�ȡ����
	 * @param sign
	 * @return
	 */
	public static ProjectCase cgetCaseBysign(String sign) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseByCaseSign.do?caseSign=" + sign);
		ProjectCase projectCase = JSONObject.parseObject(result, ProjectCase.class);
		return projectCase;
	}

	/**
	 * ͨ������ID��ȡ����
	 * @param sign
	 * @return
	 */
	public static ProjectCase cGetCaseByCaseId(Integer caseId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseByCaseId.do?caseId=" + caseId);
		ProjectCase projectCase = JSONObject.parseObject(result, ProjectCase.class);
		return projectCase;
	}
	
	/**
	 * ��ȡ��Ŀ�µ����й�������
	 * @param projectid
	 * @return
	 */
	public static List<ProjectCaseParams> cgetParamsByProjectid(String projectid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetParamsByProjectId.do?projectId="+projectid);
		List<ProjectCaseParams> paramsList = JSONObject.parseArray(result, ProjectCaseParams.class);
		return paramsList;
	}
	
	/**
	 * ͨ���ƻ�ID��ȡ������������
	 * @param planid
	 * @return
	 */
	public static List<Integer> clientGetCaseListForUnSucByTaskId(Integer taskId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListForUnSucByTaskId.do?taskId=" + taskId);		
        List<Integer> caseIdList = JSONObject.parseArray(result, Integer.class);
		return caseIdList;
	}
	
	/**
	 * ͨ��templateId��ȡʵ��
	 * @param templateId
	 * @return
	 * @author Seagull
	 * @date 2019��4��24��
	 */
	public static ProjectProtocolTemplate clientGetProjectProtocolTemplateByTemplateId(Integer templateId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetProjectProtocolTemplateByTemplateId.do?templateId=" + templateId);
		ProjectProtocolTemplate projectProtocolTemplate = JSONObject.parseObject(result, ProjectProtocolTemplate.class);
		return projectProtocolTemplate;
	}
	
	/**
	 * ͨ��ģ��ID��ȡ�����б�
	 * @param templateId
	 * @return
	 * @author Seagull
	 * @date 2019��4��24��
	 */
	public static List<ProjectTemplateParams> clientGetProjectTemplateParamsListByTemplateId(Integer templateId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetProjectTemplateParamsListByTemplateId.do?templateId=" + templateId);
		List<ProjectTemplateParams> projectTemplateParamsList = JSONObject.parseArray(result, ProjectTemplateParams.class);
		return projectTemplateParamsList;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {

	}

}
