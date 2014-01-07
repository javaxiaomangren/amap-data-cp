/**
 * 2013-11-7
 */
package com.amap.data.save.match;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

/**
 * 调用情涛聚合服务的新匹配,简单传入参数
 */
public class Match {
	private static String method = "merge";
	private static String url = "http://192.168.3.104/amap_merge?method="
			+ method + "&";

	private static String charset = "utf-8";

	/**
	 * 通过传入的String参数获取并返回匹配对应的poiid，如果没有匹配上 则返回null; 返回格式为poiid
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({})
	public static String getMatchPoiid(String data, boolean flag)
			throws Exception {
		String result = null;
		String m = getString(data);
		m = url + m;
		result = HttpclientUtil.get(m);
		return result;
	}

	/**
	 * 拼装需要的String
	 * 
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings({ "rawtypes" })
	private static String getString(String data)
			throws UnsupportedEncodingException {
		String result = "";

		Map combineMap = (Map) JSON.parse(data);

		result += "source=" + combineMap.get("src_type");
		result += "&poiid=" + combineMap.get("poiid");
		result += "&x=" + combineMap.get("x");
		result += "&y=" + combineMap.get("y");
		result += "&name="
				+ URLEncoder.encode(combineMap.get("name").toString(), charset);
		result += "&addr="
				+ URLEncoder.encode(combineMap.get("addr").toString(), charset);
		result += "&tel=" + combineMap.get("tel");
		result += "&type=" + combineMap.get("type");
		result += "&code=" + combineMap.get("code");

		// 省市区
		result += "&province="
				+ URLEncoder.encode(combineMap.get("province").toString(),
						charset);
		result += "&city="
				+ URLEncoder.encode(combineMap.get("city").toString(), charset);
		result += "&district="
				+ URLEncoder.encode(combineMap.get("district").toString(),
						charset);
		return result;
	}

	public static void main(String[] args) throws Exception {
		String data = " {\"poiid\":\"B02C901G5C\",\"x\":\"114.598786\", \"y\":\"31.272068\", \"code\":\"421122\", \"type\":\"010101\",\"name\":\"加油站(红安县城关镇人民政府西南)\",\"addr\":\"\",\"tel\":\"\",\"province\":\"湖北省\", \"city\":\"黄冈市\",\"district\":\"红安县\"}";
		String result = getMatchPoiid(data, false);
		System.out.println(result);
	}
}
