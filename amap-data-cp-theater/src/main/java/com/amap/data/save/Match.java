/**
 * 2013-8-20
 */
package com.amap.data.save;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

/**
 * 针对新增数据 调用匹配接口
 */
public class Match {
	private static int doType = 2;
	private static String url = "http://10.2.134.101:8081/poiMatch/match_cp?doType=" + doType + "&";

	//flag为true代表测试环境；false为线上
	private static void init(boolean flag){
		if(!flag){
			url = "http://192.168.3.214:8080/poiMatch/match_cp?doType=" + doType + "&";
		}
	}
	/**
	 * 通过传入的map参数获取并返回匹配对应的poiid，如果没有匹配上 则返回null; 返回格式为poiid,type
	 * 
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static String getMatchPoiid(String data, boolean flag)
			throws UnsupportedEncodingException {
		init(flag);
		String combineUrl = getHttpUrl(data);
		if (combineUrl == null) {
			return null;
		}
		String result = null;
		result = HttpclientUtil.get(url + combineUrl);
		String poiid = assertResult(result);
		return poiid == null ? null : poiid;
	}
	
	/**
	 * 拼装url访问串,地址和经纬度至少有一个有值，别的必须有值不能为空
	 * 
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	private static String getHttpUrl(String combineJson)
			throws UnsupportedEncodingException {
		Map combineMap = (Map) JSON.parse(combineJson);
		Object baseObj = combineMap.get("base");
		if(baseObj == null || baseObj.equals("")){
			return null;
		}
		Map base = (Map) JSON.parse(baseObj.toString());
		
		Object fromObj = combineMap.get("from");
		Map from = (Map) JSON.parse(fromObj.toString());
		String name = null;
		if (base.get("name") != null) {
			name = URLEncoder.encode(base.get("name").toString(), "gbk");
		}
		if (name == null || name.equals("")) {
			return null;
		}
		String address = null;
		if (base.get("address") != null) {
			address = URLEncoder.encode(base.get("address").toString(), "gbk");
		}
		String tempurl = "cp=" + from.get("src_type") + "&cpid=" + from.get("src_id")
				+ "&name=" + name + "&address=" + address;
		// 地址和经纬度必须有一个有值
		if (address == null && (base.get("x") == null || base.get("y") == null)) {
			return null;
		}
		return tempurl + "&x=" + base.get("x") + "&y=" + base.get("y")
				+ "&big_type=" + base.get("new_type");
	}
	
	/**
	 * 判断返回的结果类型； 1，后面有值，代表返回的是匹配上的poiid，否则代表没有匹配上
	 */
	private static String assertResult(String result) {
		if (result != null && !result.startsWith("0")) {
			result = result.replace("\r\n", "");
			if ((result.startsWith("1") || result.startsWith("2") )&& !result.endsWith(",")) {
				if (result.split(",")[1] != null
						&& !result.split(",")[1].equals("")) {
					return result.split(",")[1];
				}
			}
		}
		return null;
	}
}
