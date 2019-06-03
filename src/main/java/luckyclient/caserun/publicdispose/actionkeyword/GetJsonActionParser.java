package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ��ȡJSON�ַ���ָ��Key��ֵ��
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="getjv")
public class GetJsonActionParser implements ActionKeyWordParser {


    /**
     * ��ȡJSON�ַ���ָ��Key��ֵ��
     * @param actionKeyWord �����ؼ���
     * @param testResult ���Խ��
     */
    @Override
    public String parse(String actionParams, String testResult) {
        String key="";
        String index="1";
        if(actionParams.endsWith("]")&&actionParams.contains("[")){
            key=actionParams.substring(0,actionParams.lastIndexOf("["));
            index=actionParams.substring(actionParams.lastIndexOf("[")+1, actionParams.lastIndexOf("]"));
            testResult= SubString.getJsonValue(testResult, key, index);
        }else{
            key=actionParams;
            testResult=SubString.getJsonValue(testResult, key, index);
        }
        LogUtil.APP.info("Action(getJV):��ȡJSON�ַ���ָ��Key��ֵ��:{}",testResult);
        return testResult;
    }
}
