package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺʹ��jsonpath����json�ַ���
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019��8��26��
 */
@Action(name="jsonpath")
public class JsonPathActionParser implements ActionKeyWordParser {


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
