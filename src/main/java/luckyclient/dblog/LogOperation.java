package luckyclient.dblog;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.DBOperation;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.api.PostServerAPI;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @ClassName: LogOperation
 * @Description: ��־д�����ݿ� @author�� seagull
 * @date 2015��4��15�� ����9:29:40
 * 
 */
public class LogOperation {
	public static DBOperation dbt = DbLink.dbLogLink();
	static int exetype = DbLink.exetype;

	/**
	 * ��������ִ��״̬ 0ͨ�� 1ʧ�� 2���� 3ִ���� 4δִ��
	 */
	public void insertTaskCaseExecute(String taskIdStr, Integer projectId,Integer caseId,  String caseSign,String caseName, Integer caseStatus) {
		if (0 == exetype) {
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientPostInsertTaskCaseExecute(taskId, projectId, caseId, caseSign, caseName, caseStatus);
		}
	}

	/**
	 * ��������ִ��״̬ 0ͨ�� 1ʧ�� 2���� 3ִ���� 4δִ��
	 */
	public void updateTaskCaseExecuteStatus(String taskIdStr, Integer caseId, Integer caseStatus) {
		if (0 == exetype) {
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientUpdateTaskCaseExecuteStatus(taskId, caseId, caseStatus);
		}
	}

	/**
	 * ��������ִ����־
	 */
	public void insertTaskCaseLog(String taskIdStr, Integer caseId, String logDetail, String logGrade, String logStep,
			String imgname) {
		if (0 == exetype) {
			if (logDetail.length()>5000) {
				 luckyclient.publicclass.LogUtil.APP.info("��־��ϸ����5000�ַ����޷��������ݿ�洢��������־��ϸ��ӡ...");
				 luckyclient.publicclass.LogUtil.APP.info("��"+logStep+"������־����"+logGrade+",��־��ϸ��"+logGrade+"��...");
				 logDetail="��־��ϸ����5000�ַ��޷��������ݿ⣬����LOG4J��־�д�ӡ����ǰ���鿴...";
			}
			
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientPostInsertTaskCaseLog(taskId, caseId, logDetail, logGrade, logStep, imgname);
		}
	}

	/**
	 * ���±��������ִ��ͳ�����
	 * ״̬ 0δִ�� 1ִ���� 2ִ����� 3ִ��ʧ�� 4����ͻ���ʧ��
	 */
	public static int[] updateTaskExecuteData(String taskIdStr, int caseCount) {
		int[] taskcount = null;
		if (0 == exetype) {
			Integer taskId = Integer.parseInt(taskIdStr);
			String str = PostServerAPI.clientUpdateTaskExecuteData(taskId, caseCount,2);
			JSONObject jsonObject = JSONObject.parseObject(str);

			// ���ر�������ִ�����
			taskcount = new int[5];
			taskcount[0] = jsonObject.getInteger("caseCount");
			taskcount[1] = jsonObject.getInteger("caseSuc");
			taskcount[2] = jsonObject.getInteger("caseFail");
			taskcount[3] = jsonObject.getInteger("caseLock");
			taskcount[4] = jsonObject.getInteger("caseNoExec");

		}
		return taskcount;
	}

	/**
	 * ���±��������ִ��״̬
	 * ״̬ 0δִ�� 1ִ���� 2ִ����� 3ִ��ʧ�� 4����ͻ���ʧ��
	 */
	public static void updateTaskExecuteStatus(String taskIdStr, int caseCount) {
		if (0 == exetype) {
			Integer taskId = Integer.parseInt(taskIdStr);
			PostServerAPI.clientUpdateTaskExecuteData(taskId, caseCount,1);
		}
	}

	/**
	 * ɾ����������ָ����������־��ϸ
	 */
	public static void deleteTaskCaseLog(Integer caseId, String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		PostServerAPI.clientDeleteTaskCaseLog(taskId, caseId);
	}

	/**
	 * ȡ��ָ������ID�еĲ����ڳɹ�״̬������ID
	 */
	public List<Integer> getCaseListForUnSucByTaskId(String taskIdStr) {
		int taskId = Integer.parseInt(taskIdStr);
		return GetServerAPI.clientGetCaseListForUnSucByTaskId(taskId);
	}

	/**
	 * ȡ��ָ������ID�������ĵ����Ƿ�Ҫ�����ʼ�״̬���ռ��˵�ַ �����ʼ�֪ͨʱ�ľ����߼�, -1-��֪ͨ 0-ȫ����1-�ɹ���2-ʧ��
	 * ���� eMailer varchar(100) ; --�ռ���
	 */

	public static String[] getEmailAddress(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String[] address = null;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			if (taskScheduling.getEmailSendCondition()!=-1) {
				String temp = taskScheduling.getEmailAddress();
				// ������һ��;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// �����ַ
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					address = temp.split(";", -1);
					// һ����ַ
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					address = new String[1];
					address[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return address;
		}
		return address;
	}

	/**
	 * ȡ��ָ������ID�������ĵ����Ƿ�Ҫ�Զ������Լ���������Ŀ���� Ϊ��ʱ������
	 */
	public static String[] getBuildName(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String[] buildname = null;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			if (null == taskScheduling.getBuildingLink() || "".equals(taskScheduling.getBuildingLink())) {
				return buildname;
			}else{
				String temp = taskScheduling.getBuildingLink();
				// ������һ��;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// �������
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					buildname = temp.split(";", -1);
					// һ������
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					buildname = new String[1];
					buildname[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return buildname;
		}
		return buildname;
	}

	/**
	 * ȡ��ָ������ID�������ĵ����Ƿ�Ҫ�Զ�����TOMCAT
	 * �Զ����� restartcomm varchar(200) ; -- ��ʽ��������IP;�������û���;����������;ssh�˿�;Shell����;
	 * ����192.168.222.22;pospsettle;pospsettle;22;cd
	 * /home/pospsettle/tomcat-7.0-7080/bin&&./restart.sh;
	 */

	public static String[] getRestartComm(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String[] command = null;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			if (null == taskScheduling.getRemoteShell() || "".equals(taskScheduling.getRemoteShell())) {
				return command;
			}else{
				String temp = taskScheduling.getRemoteShell();
				// ������һ��;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// �������
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					command = temp.split(";", -1);
					// һ������
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					command = new String[1];
					command[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return command;
		}
		return command;

	}

	/**
	 * ��ȡ�������ʱ��
	 */
	public static String getTestTime(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		String desTime = "�������ʱ������";
		try {
			TaskExecute taskExecute = GetServerAPI.cgetTaskbyid(taskId);
			Date start = taskExecute.getCreateTime();
            if (null!= taskExecute.getFinishTime()) {
                Date finish = taskExecute.getFinishTime();
                long l = finish.getTime() - start.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                desTime = "<font color='#2828FF'>" + hour + "</font>Сʱ<font color='#2828FF'>" + min
                        + "</font>��<font color='#2828FF'>" + s + "</font>��";
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return desTime;
		}
		return desTime;
	}

	/**
	 * ��ѯwebִ�У����������  UI�Զ������������ 0 IE 1 ��� 2 �ȸ� 3 Edge
	 */
	public static int querydrivertype(String taskIdStr) {
		Integer taskId = Integer.parseInt(taskIdStr);
		Integer driverType = 0;
		try {
			TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(taskId);
			driverType = taskScheduling.getBrowserType();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return driverType;
		}
		return driverType;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	}

}
