package springboot;

import java.io.File;
import java.net.InetAddress;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import luckyclient.netty.NettyClient;
import luckyclient.utils.config.SysConfig;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @author seagull
 * @date 2018��7��27�� ����10:16:40
 */
@SpringBootApplication
public class RunService {

	private static final Logger log = LoggerFactory.getLogger(RunService.class);
	private static final String NETTY_SERVER_IP= SysConfig.getConfiguration().getProperty("netty.server.ip");
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator +"bootlog4j.conf");
		SpringApplication.run(RunService.class, args);
		try {
			String host = InetAddress.getLocalHost().getHostAddress();
			log.info("�����ͻ��˼���,���Ժ�......����IP:{}",host);
		} catch (Exception e) {
			log.error("��ȡ����IP�����쳣......", e);
		}
		if(NETTY_SERVER_IP==null)
			HttpImpl.checkHostNet();
		try {
			log.info("##################�ͻ���netty����#################");
			NettyClient.start();
		} catch (Exception e) {
			log.error("���ӷ����netty�쳣......");
			e.printStackTrace();
		}
	}

}
