package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ��ȡJSON�ַ���ָ��Key��ֵ����������ƥ��
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subStrRgex")
public class SubStrRegxActionParser implements ActionKeyWordParser {


    /**
     * ��ȡJSON�ַ���ָ��Key��ֵ����������ƥ��
     * @param actionKeyWord �����ؼ���
     * @param testResult ���Խ��
     */
    @Override
    public String parse(String actionKeyWord, String testResult) {
        String actionparams=actionKeyWord.substring(0, actionKeyWord.lastIndexOf("#subStrRgex"));
        String key="";
        String index="1";
        if(actionparams.endsWith("]")&&actionparams.contains("[")){
            key=actionparams.substring(0,actionparams.lastIndexOf("["));
            index=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
            testResult= SubString.subStrRgex(testResult, key, index);
        }else{
            key=actionparams;
            testResult= SubString.subStrRgex(testResult, key, index);
        }
        LogUtil.APP.info("Action(subStrRgex):��ȡJSON�ַ���ָ��Key��ֵ�ǣ�"+testResult);
        return testResult;
    }
}
