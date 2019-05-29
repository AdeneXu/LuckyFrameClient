package luckyclient.caserun.publicdispose.actionkeyword;


import com.alibaba.fastjson.JSONObject;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ����Ӧheader��ȡ��ĳ��headerֵ
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="Header")
public class HeaderParser implements ActionKeyWordParser {


    /**
     * @param actionorder �����ؼ���
     */
    @Override
    public String parse(String actionorder, String testResult) {


        // ��ȡ�����ȴ�ʱ��
        String headerParam=actionorder.substring(0, actionorder.lastIndexOf("#Header"));
        String pre = "RESPONSE_HEAD:��";
        String headerStr = testResult.substring(testResult.indexOf(pre) + pre.length(), testResult.indexOf("�� RESPONSE_CODE"));
        return JSONObject.parseObject(headerStr).getJSONArray(headerParam).getString(0);

    }
}
