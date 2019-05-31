package luckyclient.caserun.publicdispose.actionkeyword;


import com.alibaba.fastjson.JSONObject;

/**
 * �����ؼ��ֵĴ���ӿڵ�ʵ���ࣺ����Ӧheader��ȡ��ĳ��headerֵ
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="header")
public class HeaderParser implements ActionKeyWordParser {


    /**
     * @param actionorder �����ؼ���
     */
    @Override
    public String parse(String actionParams, String testResult) {
        String pre = "RESPONSE_HEAD:��";
        String headerStr = testResult.substring(testResult.indexOf(pre) + pre.length(), testResult.indexOf("�� RESPONSE_CODE"));
        return JSONObject.parseObject(headerStr).getJSONArray(actionParams).getString(0);

    }
}
