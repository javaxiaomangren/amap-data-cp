/**
 * 2013-8-21
 */
package com.amap.data.save.picDeal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

/**
 * 图片处理：从图片服务器获取图片信息
 */
public class PicDeal {
//	private final static Logger log = LoggerFactory.getLogger(PicDeal.class);
	
	private static String  urlString2 = "http://192.168.3.200:8087/image/service";
	private static String  urlString1 = "http://192.168.3.201:8087/image/service";
//	private static String  urlString = "http://10.2.140.17:8080/image/service";

	private static String  picurlString = "http://store.is.autonavi.com/showpic/";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getMapAfterPicDeal(Object data){
		if(data == null){
			return data;
		}
		//先装成map，看是否能转
		try{
			//针对基础和深度
			LinkedHashMap info = (LinkedHashMap)data;
			data = getPicAfterTrans(info);
		}catch (Exception e) {
		}
		try{
			//针对动态：动态是个list
			List<LinkedHashMap> infos = (List<LinkedHashMap>) data;
			List<LinkedHashMap> newInfos = new ArrayList<LinkedHashMap>();
			for(LinkedHashMap info : infos){
				newInfos.add(getPicAfterTrans(info));
			}
			data = newInfos;
		}catch (Exception e) {
		}
		return data;
	}
	
	/**
	 * 处理图片
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap getPicAfterTrans(LinkedHashMap info){
		if(info == null || !info.containsKey("pic_info")){
			return info;
		}
		Object pics = info.get("pic_info");
		String picsJson = JSON.serialize(pics);
		Map m = new HashMap();
		m.put("data", picsJson);
		String result = null;
		try {
			result = HttpclientUtil.post(urlString1, m, "UTF-8");
		} catch (Exception e) {
		}
		if(result == null || result.equals("")){
			try {
				result = HttpclientUtil.post(urlString2, m, "UTF-8");
			} catch (Exception e) {
			}
			if(result == null || result.equals("")){
				return info;
			}
		}
		Map resultMap = (Map) JSON.parse(result);

		//根据原始pic_info和返回结果，拼装新的图片信息
		List<LinkedHashMap> oldPics = (List<LinkedHashMap>) info.get("pic_info");
		List<LinkedHashMap> newPics = (List<LinkedHashMap>) resultMap.get("urldata");
		List<LinkedHashMap> resultPics = new ArrayList<LinkedHashMap>();
		for(int i = 0; i < oldPics.size(); i++){
			LinkedHashMap pic = oldPics.get(i);
			LinkedHashMap newPic = newPics.get(i);
			if("1".equals(newPic.get("state") + "")){
				String pic_id = newPic.get("md5").toString();
				pic.put("url", picurlString + pic_id);
				pic.put("fetch_type", "1");
				pic.put("pic_id", pic_id);
			} 
			resultPics.add(pic);
		}
		info.put("pic_info", resultPics);
		return info;
	}
}
