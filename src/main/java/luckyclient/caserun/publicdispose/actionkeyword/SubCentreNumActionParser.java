package luckyclient.caserun.publicdispose.actionkeyword;


import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ��ȡ���Խ��ָ����ʼ������λ���ַ���
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subCentreNum")
public class SubCentreNumActionParser implements ActionKeyWordParser {


    /**
     * ��ȡ���Խ��ָ����ʼ������λ���ַ���
     * @param actionKeyWord �����ؼ���
     * @param testResult ���Խ��
     */
    @Override
    public String parse(String actionKeyWord, String testResult) {
        String actionParams=actionKeyWord.substring(0, actionKeyWord.lastIndexOf("#subCentreNum"));
        
        if(actionParams.startsWith("[")&&actionParams.endsWith("]")){
            String startnum=actionParams.substring(actionParams.indexOf("[")+1, actionParams.indexOf("]"));
            String endnum=actionParams.substring(actionParams.lastIndexOf("[")+1, actionParams.lastIndexOf("]"));
            testResult= SubString.subCentreNum(testResult, startnum, endnum);
            LogUtil.APP.info("Action(subCentreNum):��ȡ���Խ��ָ����ʼ������λ���ַ�����"+testResult);
        }else{
            testResult="���趯����subCentreNum ������[\"��ʼ�ַ�\"][\"�����ַ�\"]#subCentreNum ��ʽ���������Ĳ��趯���ؼ���:"+actionKeyWord;
            LogUtil.APP.error("���趯����subCentreNum ������[\"��ʼλ��(����)\"][\"����λ��(����)\"]#subCentreNum ��ʽ���������Ĳ��趯���ؼ���:"+actionKeyWord);
        }
        return testResult;
    }
}
