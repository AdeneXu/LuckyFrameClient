package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ��ȡJSON�ַ���ָ��Key��ֵ����������ƥ��
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="substrrgex")
public class SubStrRegxActionParser implements ActionKeyWordParser {


    /**
     * ��ȡJSON�ַ���ָ��Key��ֵ����������ƥ��
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
            testResult= SubString.subStrRgex(testResult, key, index);
        }else{
            key=actionParams;
            testResult= SubString.subStrRgex(testResult, key, index);
        }
        LogUtil.APP.info("Action(subStrRgex):��ȡJSON�ַ���ָ��Key��ֵ�ǣ�"+testResult);
        return testResult;
    }
}
