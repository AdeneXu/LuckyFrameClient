package luckyclient.publicclass.remoterinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class HttpRequest {
	final static Properties PROPERTIES = luckyclient.publicclass.SysConfig.getConfiguration();
	private final static String WEB_URL = "http://" + PROPERTIES.getProperty("server.web.ip") + ":"
			+ PROPERTIES.getProperty("server.web.port");

	/**
	 * �ַ�������
	 * 
	 * @param url
	 * @return
	 */
	public static String loadJSON(String repath) {
		String charset="GBK";
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		BufferedReader br = null;
		// �����������
		HttpGet httpGet = new HttpGet(WEB_URL+repath);
		try {
			HttpResponse response = httpclient.execute(httpGet);
			// ��ȡ��������Ӧ����
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			resultBuffer = new StringBuffer();
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	 /**
     * ��ָ�� URL ����POST����������
     * 
     * @param url
     *            ��������� URL
     * @param param
     *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
     * @return ������Զ����Դ����Ӧ���
     */
    public static String sendPost(String repath, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(WEB_URL+repath);
            // �򿪺�URL֮�������
            URLConnection conn = realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����POST�������������������
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // ��ȡURLConnection�����Ӧ�������������utf-8����
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "GBK"));
            // �����������
            out.print(param);
            // flush������Ļ���
            out.flush();
            // ����BufferedReader����������ȡURL����Ӧ,����utf-8����
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("���� POST ��������쳣��"+e);
            e.printStackTrace();
        }
        //ʹ��finally�����ر��������������
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

	/**
	 * ʹ��HttpClient��JSON��ʽ����post����
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String httpClientPostJson(String urlParam, String params){		
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient=HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(WEB_URL+urlParam);
	    httpPost.setHeader("Content-Type", "application/json");
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(60*1000)
	            .setConnectionRequestTimeout(60*1000)  
	            .setSocketTimeout(60*1000).build();  //��������ʹ��䳬ʱʱ��
	    httpPost.setConfig(requestConfig);
		// �����������
		BufferedReader br = null;
		try {
		StringEntity entity = new StringEntity(params,"utf-8");
		httpPost.setEntity(entity);
       
		 CloseableHttpResponse response = httpclient.execute(httpPost);

		// ��ȡ��������Ӧ����
		resultBuffer = new StringBuffer();
		if(null!=response.getEntity()){
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}	
		}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return resultBuffer.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
