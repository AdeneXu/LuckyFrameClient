package luckyclient.serverapi.api;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.remoterinterface.HttpRequest;
import luckyclient.serverapi.entity.ProjectCaseDebug;
import luckyclient.serverapi.entity.TaskCaseExecute;
import luckyclient.serverapi.entity.TaskCaseLog;


/**
 * 
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019��4��18��
 */
public class PostServerAPI {
	
	private static final String prefix = "/openPostApi";
	
	/**
	 * put web��������ݵ������
	 * @param sign
	 * @param executor
	 * @param loglevel
	 * @param detail
	 */
	public static void cPostDebugLog(Integer userId, Integer caseId, String logLevel, String logDetail,Integer debugIsend){
		ProjectCaseDebug ProjectCaseDebug = new ProjectCaseDebug();
		ProjectCaseDebug.setCaseId(caseId);
		ProjectCaseDebug.setUserId(userId);
		ProjectCaseDebug.setLogLevel(logLevel);
		ProjectCaseDebug.setLogDetail(logDetail);
		ProjectCaseDebug.setDebugIsend(debugIsend);
		
		HttpRequest.httpClientPostJson(prefix+"/clientPostCaseDebugLog", JSONObject.toJSONString(ProjectCaseDebug));
	}

	/**
	 * ��������ִ����ϸ�����ݿ�
	 * @param taskId
	 * @param projectId
	 * @param caseId
	 * @param caseSign
	 * @param caseName
	 * @param caseStatus
	 * @author Seagull
	 * @date 2019��4��22��
	 */
	public static void clientPostInsertTaskCaseExecute(Integer taskId, Integer projectId, Integer caseId, String caseSign, String caseName, Integer caseStatus){
		TaskCaseExecute taskCaseExecute = new TaskCaseExecute();
		taskCaseExecute.setTaskId(taskId);
		taskCaseExecute.setProjectId(projectId);
		taskCaseExecute.setCaseId(caseId);
		taskCaseExecute.setCaseSign(caseSign);
		taskCaseExecute.setCaseName(caseName);
		taskCaseExecute.setCaseStatus(caseStatus);
		taskCaseExecute.setCreateTime(new Date());
		taskCaseExecute.setUpdateTime(new Date());
		
		HttpRequest.httpClientPostJson(prefix+"/clientPostTaskCaseExecute", JSONObject.toJSONString(taskCaseExecute));
	}
	
	/**
	 * �޸�����ִ��״̬
	 * @param taskId
	 * @param caseId
	 * @param caseStatus
	 * @author Seagull
	 * @date 2019��4��22��
	 */
	public static void clientUpdateTaskCaseExecuteStatus(Integer taskId, Integer caseId, Integer caseStatus){
		TaskCaseExecute taskCaseExecute = new TaskCaseExecute();
		taskCaseExecute.setTaskId(taskId);
		taskCaseExecute.setCaseId(caseId);
		taskCaseExecute.setCaseStatus(caseStatus);
		taskCaseExecute.setUpdateTime(new Date());
		
		HttpRequest.httpClientPostJson(prefix+"/clientUpdateTaskCaseExecuteStatus", JSONObject.toJSONString(taskCaseExecute));
	}
	
	/**
	 * ��������ִ����ϸ�����ݿ�
	 * @param taskId
	 * @param caseId
	 * @param logDetail
	 * @param logGrade
	 * @param logStep
	 * @param imgname
	 * @author Seagull
	 * @date 2019��4��22��
	 */
	public static void clientPostInsertTaskCaseLog(Integer taskId, Integer caseId, String logDetail, String logGrade, String logStep,
			String imgname){
		TaskCaseLog taskCaseLog = new TaskCaseLog();
		taskCaseLog.setTaskId(taskId);
		taskCaseLog.setCaseId(caseId);
		taskCaseLog.setLogDetail(logDetail);
		taskCaseLog.setLogGrade(logGrade);
		taskCaseLog.setLogStep(logStep);
		taskCaseLog.setImgname(imgname);
		taskCaseLog.setCreateTime(new Date());
		taskCaseLog.setUpdateTime(new Date());
		
		HttpRequest.httpClientPostJson(prefix+"/clientPostTaskCaseLog", JSONObject.toJSONString(taskCaseLog));
	}
	
	/**
	 * ��������ִ������
	 * @param taskId
	 * @param casecount
	 * @author Seagull
	 * @date 2019��4��22��
	 */
	public static String clientUpdateTaskExecuteData(Integer taskId, Integer caseCount, Integer taskStatus){
		String str = "{\"taskId\":"+taskId+",\"caseCount\":"+caseCount+",\"taskStatus\":"+taskStatus+"}";
		JSONObject jsonObject = JSON.parseObject(str);
		return HttpRequest.httpClientPostJson(prefix+"/clientUpdateTaskExecuteData", jsonObject.toJSONString());
	}
	
	/**
	 * ��������ִ������
	 * @param taskId
	 * @param casecount
	 * @author Seagull
	 * @date 2019��4��22��
	 */
	public static String clientDeleteTaskCaseLog(Integer taskId, Integer caseId){
		String str = "{\"taskId\":"+taskId+",\"caseId\":"+caseId+"}";
		JSONObject jsonObject = JSON.parseObject(str);
		return HttpRequest.httpClientPostJson(prefix+"/clientDeleteTaskCaseLog", jsonObject.toJSONString());
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {

	}

}
