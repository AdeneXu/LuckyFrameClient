package luckyclient.caserun.publicdispose;

import luckyclient.caserun.publicdispose.actionkeyword.Action;
import luckyclient.caserun.publicdispose.actionkeyword.ActionKeyWordParser;
import luckyclient.publicclass.LogUtil;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ���趯�����������ݲ������ɲ�ͬ�Ķ����ؼ������ͣ�ִ����Ӧ�Ľ���
 * @author: sunshaoyan@
 * @date: Created on 2019/4/13
 */
public class ActionContext {

    private static Logger logger = LoggerFactory.getLogger(ActionContext.class);

    private static Map<String, Class> allActions;

    static {
        Reflections reflections = new Reflections("luckyclient.caserun.publicdispose.actionkeyword");
        Set<Class<?>> annotatedClasses =
                reflections.getTypesAnnotatedWith(Action.class);
        allActions = new ConcurrentHashMap<String, Class>();
        for (Class<?> classObject : annotatedClasses) {
            Action action = (Action) classObject
                    .getAnnotation(Action.class);
            allActions.put(action.name(), classObject);
        }
        allActions = Collections.unmodifiableMap(allActions);
    }

    private ActionKeyWordParser action;

    public ActionContext(String name){

        if (allActions.containsKey(name)) {
            logger.info("Created Action name is {}", name);
            try {
                action = (ActionKeyWordParser) allActions.get(name).newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                logger.error("Instantiate Action failed", ex);
            }
        } else {
            logger.error("Specified Action name {} does not exist", name);
        }
    }

    public String parse(String actionKeyWord,String testResult) {
        if(null != action){
            testResult = action.parse(actionKeyWord, testResult);
        }else {
            testResult="δ��������Ӧ�����ؼ��֣�ֱ�������˶���������ؼ��֣�"+actionKeyWord;
            LogUtil.APP.error("δ��������Ӧ�����ؼ��֣�ֱ�������˶���������ؼ��֣�"+actionKeyWord);
        }
        return testResult;
    }

}
