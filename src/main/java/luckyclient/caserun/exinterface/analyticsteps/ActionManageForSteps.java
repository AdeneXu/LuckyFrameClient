package luckyclient.caserun.exinterface.analyticsteps;

import luckyclient.publicclass.ChangString;

public class ActionManageForSteps {
	/**
	 * ������������
	 * @param projectcase
	 * @param step
	 * @param taskid
	 * @param caselog
	 * @return
	 */
	public static String actionManage(String stepsaction,String testresult){
		luckyclient.publicclass.LogUtil.APP.info("���Խ���ǣ�" + testresult);
		luckyclient.publicclass.LogUtil.APP.info("���ڽ��뵽Action(����)����......ACTIONֵ��"+stepsaction);
		if(null==stepsaction||"".equals(stepsaction.trim())){
			luckyclient.publicclass.LogUtil.APP.info("Action(����)�������账��......");
			return testresult;
		}
		stepsaction=stepsaction.toLowerCase().trim();
		String[] temp=stepsaction.split("\\|",-1);
		for(String actionorder:temp){
			testresult=actionExecute(actionorder,testresult);
		}
		return testresult;
	}
	
	private static String actionExecute(String actionorder,String testresult){
        // �������¼�
		if(actionorder.endsWith("*wait")){
			if(ChangString.isInteger(actionorder.substring(0, actionorder.lastIndexOf("*wait")))){
	            try {
	                // ��ȡ�����ȴ�ʱ��
	                int time=Integer.parseInt(actionorder.substring(0, actionorder.lastIndexOf("*wait")));
	                if (time > 0) {
	    					Thread.sleep(time * 1000);
	                }
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
			}else{
				luckyclient.publicclass.LogUtil.APP.error("�ȴ�����������ֱ�������˶��������飡");
			}
		}
        return testresult;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
