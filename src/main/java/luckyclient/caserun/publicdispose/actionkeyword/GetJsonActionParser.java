package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ��ȡJSON�ַ���ָ��Key��ֵ��
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="getJV")
public class GetJsonActionParser implements ActionKeyWordParser {


    /**
     * ��ȡJSON�ַ���ָ��Key��ֵ��
     * @param actionKeyWord �����ؼ���
     * @param testResult ���Խ��
     */
    @Override
    public String parse(String actionKeyWord, String testResult) {
        String actionparams=actionKeyWord.substring(0, actionKeyWord.lastIndexOf("#getJV"));
        String key="";
        String index="1";
        if(actionparams.endsWith("]")&&actionparams.contains("[")){
            key=actionparams.substring(0,actionparams.lastIndexOf("["));
            index=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
            testResult= SubString.getJsonValue(testResult, key, index);
        }else{
            key=actionparams;
            testResult=SubString.getJsonValue(testResult, key, index);
        }
        LogUtil.APP.info("Action(getJV):��ȡJSON�ַ���ָ��Key��ֵ�ǣ�"+testResult);
        return testResult;
    }
}
