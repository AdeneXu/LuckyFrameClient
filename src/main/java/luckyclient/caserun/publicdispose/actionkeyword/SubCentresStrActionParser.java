package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ��ȡ���Խ��ָ����ʼ������λ���ַ���
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subCentreStr")
public class SubCentresStrActionParser implements ActionKeyWordParser {


    /**
     * ��ȡ���Խ��ָ����ʼ������λ���ַ���
     * @param actionKeyWord �����ؼ���
     * @param testResult ���Խ��
     */
    @Override
    public String parse(String actionKeyWord, String testResult) {
        String actionparams=actionKeyWord.substring(0, actionKeyWord.lastIndexOf("#subCentreStr"));
        String startstr="";
        String endstr="";
        if(actionparams.startsWith("[")&&actionparams.endsWith("]")){
            startstr=actionparams.substring(actionparams.indexOf("[")+1, actionparams.indexOf("]"));
            endstr=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
            testResult= SubString.subCentreStr(testResult, startstr, endstr);
            LogUtil.APP.info("Action(subCentreStr):��ȡ���Խ��ָ����ʼ������λ���ַ�����"+testResult);
        }else{
            testResult="���趯����subCentreStr ������[\"��ʼ�ַ�\"][\"�����ַ�\"]#subCentreStr ��ʽ���������Ĳ��趯���ؼ���:"+actionKeyWord;
            LogUtil.APP.error("���趯����subCentreStr ������[\"��ʼ�ַ�\"][\"�����ַ�\"]#subCentreStr ��ʽ���������Ĳ��趯���ؼ���:"+actionKeyWord);
        }
        return testResult;
    }
}
