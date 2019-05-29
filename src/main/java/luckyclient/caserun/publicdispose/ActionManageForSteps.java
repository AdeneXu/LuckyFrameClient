package luckyclient.caserun.publicdispose;

import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ִ���
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull
 * =================================================================
 * @author Seagull
 * @date 2019��1��15��
 */
public class ActionManageForSteps {

	/**
	 * ������������
	 * @param stepsaction
	 * @param testresult
	 * @return
	 */
	public static String actionManage(String stepsaction,String testresult){
		LogUtil.APP.info("���Խ���ǣ�" + testresult);
		LogUtil.APP.info("���ڽ��뵽Action(����)����......ACTIONֵ��"+stepsaction);
		if(null==stepsaction||"".equals(stepsaction.trim())){
			LogUtil.APP.info("Action(����)���账��......");
			return testresult;
		}
		stepsaction=stepsaction.trim();
		String[] temp=stepsaction.split("\\|",-1);
		for(String actionorder:temp){
			if(null!=actionorder&&!"".equals(actionorder.trim())){
				testresult=actionExecute(actionorder,testresult);
			}
		}
		return testresult;
	}

	/**
	 * �����ؼ���ִ��
	 * @param actionKeyWord
	 * @param testResult
	 * @return
	 */
	private static String actionExecute(String actionKeyWord,String testResult){
		try{

			String[] actionArr = actionKeyWord.split("#");
			if(actionArr.length == 2){
				ActionContext actionContext = new ActionContext(actionArr[1]);
				testResult = actionContext.parse(actionKeyWord, testResult);
			}else {
				testResult="�ؼ����﷨��д��������ؼ��֣�"+actionKeyWord;
				LogUtil.APP.error("�ؼ����﷨��д��������ؼ��֣�"+actionKeyWord);
			}
			return testResult;
		}catch(Exception e){
			testResult="�����趯���¼������г����쳣��ֱ�ӷ��ز��Խ����"+actionKeyWord;
			LogUtil.APP.error("�����趯���¼������г����쳣��ֱ�ӷ��ز��Խ����" ,e);
			return testResult;
		}
	}

}
