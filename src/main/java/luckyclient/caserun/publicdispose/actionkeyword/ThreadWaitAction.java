package luckyclient.caserun.publicdispose.actionkeyword;


import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ�̵߳ȴ�ʱ��
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="Wait")
public class ThreadWaitAction implements ActionKeyWordParser {


    /**
     * @param actionorder �����ؼ���
     */
    @Override
    public String parse(String actionorder, String testResult) {
        if(ChangString.isInteger(actionorder.substring(0, actionorder.lastIndexOf("#Wait")))){
            try {
                // ��ȡ�����ȴ�ʱ��
                int time=Integer.parseInt(actionorder.substring(0, actionorder.lastIndexOf("#Wait")));
                if (time > 0) {
                    LogUtil.APP.info("Action(Wait):�̵߳ȴ�"+time+"��...");
                    Thread.sleep(time * 1000);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            LogUtil.APP.error("ʹ�õȴ��ؼ��ֵĲ�������������ֱ�������˶��������飡");
        }
        return testResult;
    }
}
