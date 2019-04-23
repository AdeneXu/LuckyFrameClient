package luckyclient.caserun.exinterface;

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
public class ThreadForBatchCase extends Thread{
	
	private String projectname;
	private String testCaseExternalId;
	private String taskid;
	
	public ThreadForBatchCase(String projectname,String testCaseExternalId,String taskid){
		this.projectname = projectname;
		this.testCaseExternalId = testCaseExternalId;
		this.taskid = taskid;
	}
	
	@Override
	public void run(){		
		 TestCaseExecution.oneCaseExecuteForTask(projectname, testCaseExternalId, taskid);
		 TestControl.THREAD_COUNT--;        //���̼߳���--�����ڼ���߳��Ƿ�ȫ��ִ����
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
