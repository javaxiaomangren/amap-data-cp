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
 * 调用情涛聚合服务的新匹配
 */
public class NewMatch {
	private static String method = "merge";
	private static String url = "http://192.168.3.104/amap_merge?method="
			+ method + "&";

	private static String charset = "utf-8";

	/**
	 * 通过传入的map参数获取并返回匹配对应的poiid，如果没有匹配上 则返回null; 返回格式为poiid
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({})
	public static String getMatchPoiid(String data, boolean flag)
			throws Exception {
		String result = null;
		String m = getString(data);
		if(m == null || m.equalsIgnoreCase("")){
			return null;
		}
		m = url + m;
		try{
			result = HttpclientUtil.get(m);
		} catch (Exception e) {
			return "false";
		}
		return assertResult(result);
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
		Object baseObj = combineMap.get("base");
		if (baseObj == null || baseObj.equals("")) {
			return null;
		}
		Map base = (Map) JSON.parse(baseObj.toString());

		Object fromObj = combineMap.get("from");
		Map from = (Map) JSON.parse(fromObj.toString());

		result += "source=" + from.get("src_type");
		result += "&poiid=" + from.get("src_id");
		result += "&x=" + base.get("x");
		result += "&y=" + base.get("y");
		result += "&name="
				+ URLEncoder.encode(base.get("name").toString(), charset);
		result += "&addr="
				+ URLEncoder.encode(base.get("address") + "", charset);
		result += "&tel=" + base.get("telephone");
		result += "&type=" + base.get("new_type");
		result += "&code=" + base.get("code");

		// 省市区 +
		Object admObj = base.get("admin");
		Map admin = (Map) JSON.parse(admObj.toString());
		result += "&province="
				+ URLEncoder.encode(admin.get("adm1_chn") + "", charset);
		result += "&city="
				+ URLEncoder.encode(admin.get("adm8_chn") + "", charset);
		result += "&district="
				+ URLEncoder.encode(admin.get("adm9_chn") + "", charset);
		return result;
	}

	/**
	 * 判断返回的结果类型； 1，后面有值，代表返回的是匹配上的poiid，否则代表没有匹配上
	 */
	private static String assertResult(String result) {
		if (result != null) {
			if (result.startsWith("1") || result.startsWith("2")) {
				String[] fields = result.split(",");
				if (fields.length > 1 && fields[1] != null
						&& !fields[1].equals("")) {
					return fields[1];
				}
			} else {
				return "false";
			}
		}
		return null;
	}
}
