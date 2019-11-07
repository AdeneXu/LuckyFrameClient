package luckyclient.tool.jenkins;

import java.io.IOException;
import java.util.List;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.Range;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildCause;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ConsoleLog;
import com.offbytwo.jenkins.model.JobWithDetails;

/**
 *  * Job Build(���񹹽�) ��ز���
 * ��������� Build ��ص���Ϣ���л�ȡ�����������ȡ������־
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019��10��30��
 */
public class JobBuildApi {
 
    // Jenkins ����
    private JenkinsServer jenkinsServer;
    // http �ͻ��˶���
    //private JenkinsHttpClient jenkinsHttpClient;
 
    /**
     * ���췽���е������� Jenkins ����
     * 
     * 2019��10��30��
     */
    JobBuildApi() {
    	JenkinsConnect jenkinsConnect = new JenkinsConnect();
        // ���� Jenkins
        jenkinsServer = jenkinsConnect.connection();
        // ���ÿͻ������� Jenkins
        //jenkinsHttpClient = jenkinsConnect.getClient();
    }
 
    /**
     * ��ȡ ���һ��Build����ϸ��Ϣ
     * @param jobName
     * @author Seagull
     * @date 2019��10��30��
     */
    public void getJobLastBuildDetail(String jobName){
        try {
            // ��ȡ Job ��Ϣ
            JobWithDetails job = jenkinsServer.getJob(jobName);
            // ���������һ�α�����ʾ��
            BuildWithDetails build = job.getLastBuild().details();
            // ��ȡ��������ʾ����
            System.out.println(build.getDisplayName());
            // ��ȡ�����Ĳ�����Ϣ
            System.out.println(build.getParameters());
            // ��ȡ�������
            System.out.println(build.getNumber());
            // ��ȡ����������������δ��������ʾΪnull
            System.out.println(build.getResult());
            // ��ȡִ�й����Ļ��Ϣ
            System.out.println(build.getActions());
            // ��ȡ������������ʱ��(ms)
            System.out.println(build.getDuration());
            // ��ȡ������ʼʱ���
            System.out.println(build.getTimestamp());
            // ��ȡ����ͷ��Ϣ����������������û���������Ϣ��ʱ�����
            List<BuildCause> buildCauses = build.getCauses();
            for (BuildCause bc:buildCauses){
                System.out.println(bc.getUserId());
                System.out.println(bc.getShortDescription());
                System.out.println(bc.getUpstreamBuild());
                System.out.println(bc.getUpstreamProject());
                System.out.println(bc.getUpstreamUrl());
                System.out.println(bc.getUserName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * ��ȡ Build Log ��־��Ϣ
     */
    public void getJobBuildLog(){
        try {
            // ��ȡ Job ��Ϣ
            JobWithDetails job = jenkinsServer.getJob("test-job");
            // ���������һ�α�����ʾ��
            BuildWithDetails build = job.getLastBuild().details();
            // ��ȡ��������־���������ִ�й��������ֻ��ȡ�Ѿ�ִ�еĹ�����־
 
            // Text��ʽ��־
            System.out.println(build.getConsoleOutputText());
            // Html��ʽ��־
            System.out.println(build.getConsoleOutputHtml());
 
            // ��ȡ������־,һ����������ִ�й���������
            ConsoleLog consoleLog = build.getConsoleOutputText(0);
            // ��ȡ��ǰ��־��С
            System.out.println(consoleLog.getCurrentBufferSize());
            // �Ƿ��Ѿ�������ɣ����и�����־��Ϣ
            System.out.println(consoleLog.getHasMoreData());
            // ��ȡ��ǰ��ȡ����־��Ϣ
            System.out.println(consoleLog.getConsoleLog());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * ��ȡ����ִ�й����������־��Ϣ
     */
    public void getBuildActiveLog(){
        try {
            // ���������һ�α�����ʾ��
            BuildWithDetails build = jenkinsServer.getJob("test-job").getLastBuild().details();
            // ��ǰ��־
            ConsoleLog currentLog = build.getConsoleOutputText(0);
            // �����ǰ��ȡ��־��Ϣ
            System.out.println(currentLog.getConsoleLog());
            // ����Ƿ��и�����־,����������ѭ����ȡ
            while (currentLog.getHasMoreData()){
                // ��ȡ������־��Ϣ
                ConsoleLog newLog = build.getConsoleOutputText(currentLog.getCurrentBufferSize());
                // ���������־
                System.out.println(newLog.getConsoleLog());
                currentLog = newLog;
                // ˯��1s
                Thread.sleep(1000);
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        JobBuildApi jobBuildApi = new JobBuildApi();
        jobBuildApi.getJobLastBuildDetail("");
    }
}
