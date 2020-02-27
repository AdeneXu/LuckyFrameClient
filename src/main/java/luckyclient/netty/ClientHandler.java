package luckyclient.netty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import luckyclient.utils.config.SysConfig;

public class ClientHandler extends ChannelHandlerAdapter {

    //��application.properties�ļ��л�ȡ�õ��Ĳ���;
    private static Resource resource = new ClassPathResource("application.properties");
    private static Properties props;

    static {
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String port = props.getProperty("server.port");

    static final String ECHO_REQ = "$_";

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private static final String HOST_NAME= SysConfig.getConfiguration().getProperty("host.name");

    private static final String CLIENT_VERSION= SysConfig.getConfiguration().getProperty("client.verison");

    private static final String SERVER_IP= SysConfig.getConfiguration().getProperty("server.web.ip");

    private static final String SERVER_PORT= SysConfig.getConfiguration().getProperty("server.web.port");

    private static ChannelHandlerContext ctx;

    public ClientHandler() throws IOException {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException, InterruptedException {
       //�������Ϣ����,������յ��������񷽷�����ֱ�Ӳ���һ��http���󲢷������󵽱���
        String jsonStr=new String(msg.toString().getBytes(),"UTF-8");
        JSONObject json=new JSONObject();
        try
        {
             json= JSON.parseObject(jsonStr);

        }catch (Exception e)
        {
            System.out.println("�յ�����˷�Json��Ϣ��"+jsonStr);
            return;
        }
        System.out.println("�յ��������Ϣ��"+json.toString());
        //������Ϣ
        if("run".equals(json.get("method"))){
            try {
                //��������
                JSONObject re=new JSONObject();
                re.put("method","return");
                Result result=new Result(1,"ͬ���ȴ���Ϣ����",json.get("uuid").toString());
                //����ǵ�����������һ��HTTP����
                //��ȡ��get��������post����
                String getOrPost=json.get("getOrPost").toString();
                String urlParam="http://127.0.0.1:"+port+"/"+json.get("url").toString();
                Integer socketTimeout=Integer.valueOf(json.get("socketTimeout").toString());
                String tmpResult="";
                if("get".equals(getOrPost))
                {
                    @SuppressWarnings("unchecked")
					Map<String,Object> jsonparams = (Map<String,Object>)json.get("data");
                    //get����
                    try {
                        tmpResult=HttpRequest.httpClientGet(urlParam,jsonparams,socketTimeout);
                    } catch (Exception e) {
                        System.out.println("ת�������GET�������");
                    }
                }
                else
                {
                    String jsonparams=json.get("data").toString();
                    //post����
                    try {
                        tmpResult=HttpRequest.httpClientPost(urlParam,jsonparams,socketTimeout);
                    } catch (Exception e) {
                        System.out.println("ת�������POST�������");
                    }
                }
                result.setMessage(tmpResult);
                re.put("data",result);
                sendMessage(re.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if("download".equals(json.get("method"))){
            String loadpath=json.get("path").toString();
            String path = System.getProperty("user.dir")+loadpath;
            String fileName=json.get("fileName").toString();
            //����http���������ļ�
            try {
                HttpRequest.downLoadFromUrl("http://"+SERVER_IP+":"+SERVER_PORT+"/common/download?fileName="+fileName,fileName,path);
                //��������
                JSONObject re=new JSONObject();
                Result result=new Result(1,"�ϴ��ɹ�",json.get("uuid").toString());
                re.put("method","return");
                re.put("data",result);
                sendMessage(re.toString());
                System.out.println("�����������ɹ�,·��Ϊ��"+path+";�ļ���Ϊ��"+fileName);
            } catch (Exception e) {
                e.printStackTrace();
                //��������
                JSONObject re=new JSONObject();
                Result result=new Result(0,"�ϴ�ʧ��",json.get("uuid").toString());
                re.put("method","return");
                re.put("data",result);
                sendMessage(re.toString());
                System.out.println("����������ʧ��,·��Ϊ��"+path+";�ļ���Ϊ��"+fileName);
            }
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //���Ϳͻ��˵�¼��Ϣ
        JSONObject json=new JSONObject();
        json.put("hostName",HOST_NAME);
        json.put("version",CLIENT_VERSION);
        json.put("method","clientUp");
        ClientHandler.ctx=ctx;
        sendMessage(json.toString());
    }

    @Override
    public  void channelInactive(ChannelHandlerContext ctx)
    {
        System.out.println("�����ѶϿ������ڳ�������...");
        //ʹ�ù����ж�������
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    NettyClient.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1, TimeUnit.SECONDS);

        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                /**��������,���ֳ�����*/
                JSONObject json=new JSONObject();
                json.put("method","ping");
                json.put("hostName",HOST_NAME);
                ctx.channel().writeAndFlush(json.toString()+"$_").sync();
                //log.info("�������ͳɹ�!");
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    public static void sendMessage(String json) throws InterruptedException {
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer((json+"$_").getBytes()));
    }
}