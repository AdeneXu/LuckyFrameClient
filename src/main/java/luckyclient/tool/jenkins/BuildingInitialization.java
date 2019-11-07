package luckyclient.tool.jenkins;

import luckyclient.remote.api.serverOperation;
import luckyclient.utils.LogUtil;

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
public class BuildingInitialization {
	
	public static String booleanBuildingOver(String[] buildname) throws InterruptedException{
		String buildresult = "Status:true"+" ��Ŀȫ�������ɹ���";
		int k;
		for(int i=0;i<300;i++){
			k=0;
			for(int j=0;j<buildname.length;j++){
				String result = JenkinsBuilding.buildingResult(buildname[i]);
				if(result.indexOf("alt=\"Failed\"")>-1){
					buildresult = "��Ŀ"+buildname[i]+"����ʧ�ܣ��Զ��������˳���";
					LogUtil.APP.warn("��Ŀ��{}������ʧ�ܣ��Զ��������˳���",buildname[i]);
					break;
				}else if(result.indexOf("alt=\"Success\"")>-1){
					k++;
				}
			}
			if(buildresult.indexOf("Status:true")<=-1){
				break;
			}
			LogUtil.APP.info("���ڼ�鹹���е���Ŀ(ÿ6����һ��)��������Ҫ������Ŀ��{}������Ŀǰ�ɹ���{}����",buildname.length,k);
			if(k==buildname.length){
				break;
			}			
			Thread.sleep(6000);
		}
		return buildresult;
	}

	public static String buildingRun(String tastid) throws InterruptedException{
		String result = "Status:true"+" ��ǰ����û���ҵ���Ҫ��������Ŀ��";
		try{
		String[] buildurl = serverOperation.getBuildName(tastid);
		
		if(buildurl!=null){
			LogUtil.APP.info("׼�������õĲ�����Ŀ���й��������Եȡ�������");
			for(int i=0;i<buildurl.length;i++){
				JenkinsBuilding.sendBuilding(buildurl[i]);
			}
			//�ȴ��������
			Thread.sleep(10000);  
			result = booleanBuildingOver(buildurl);
		}else{
			LogUtil.APP.info("��ǰ����û���ҵ���Ҫ��������Ŀ��");
		}
		}catch(Exception e){
			LogUtil.APP.error("��Ŀ���������г����쳣",e);
			result = "��Ŀ���������г����쳣";
			return result;
		}
		return result;

	}

}
