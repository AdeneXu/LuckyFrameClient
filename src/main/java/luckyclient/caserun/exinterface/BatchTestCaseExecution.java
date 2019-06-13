package luckyclient.caserun.exinterface;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;

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
public class BatchTestCaseExecution {
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * �����̳߳أ����߳�ִ������
	 */
	
	public static void batchCaseExecuteForTast(String projectname,String taskid,String batchcase) throws Exception{
		int threadcount = GetServerAPI.cGetTaskSchedulingByTaskId(Integer.valueOf(taskid)).getExThreadCount();
		ThreadPoolExecutor	threadExecute	= new ThreadPoolExecutor(threadcount, 30, 3, TimeUnit.SECONDS,
	            new ArrayBlockingQueue<Runnable>(1000),
	            new ThreadPoolExecutor.CallerRunsPolicy());
		//ִ��ȫ���ǳɹ�״̬����
		if(batchcase.indexOf("ALLFAIL")>-1){ 
			//��ʼ��д��������Լ���־ģ�� 
			LogOperation caselog = new LogOperation();        
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for(int i=0;i<caseIdList.size();i++){
			   ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(caseIdList.get(i));
			   TestControl.THREAD_COUNT++;   //���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
			   threadExecute.execute(new ThreadForBatchCase(projectname,testcase.getCaseId(),taskid));
			}			
		}else{                                           //����ִ������
			String[] temp=batchcase.split("\\#");
			LogUtil.APP.info("��ǰ����ִ�������й��С�{}��������������...",temp.length);
			for(int i=0;i<temp.length;i++){
				TestControl.THREAD_COUNT++;   //���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
				threadExecute.execute(new ThreadForBatchCase(projectname,Integer.valueOf(temp[i]),taskid));
			}
		}
		//���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
		int i=0;
		while(TestControl.THREAD_COUNT!=0){
			i++;
			if(i>600){
				break;
			}
			Thread.sleep(6000);
		}
		threadExecute.shutdown();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
