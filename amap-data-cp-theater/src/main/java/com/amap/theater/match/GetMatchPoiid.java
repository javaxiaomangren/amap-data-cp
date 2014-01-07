/**
 * 2013-4-25
 */
package com.amap.theater.match;

import com.amap.base.http.HttpclientUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GetMatchPoiid {
	private static int doType = 2;
	private static String url = "http://192.168.3.214:8080/poiMatch/match_cp?doType=" + doType + "&";

	/**
	 * 通过传入的map参数获取并返回匹配对应的poiid，如果没有匹配上 则返回null; 返回格式为poiid,type
	 * 
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getMatchPoiid(Map data)
			throws UnsupportedEncodingException {
		String combineUrl = getHttpUrl(data);
		if (combineUrl == null) {
			return null;
		}
		String result = null;
		// 先以类型14调用接口，看是否能匹配上
		// 返回结果类型：1,B0327000US；0,cp_name is null；2, B0327000US
		result = HttpclientUtil.get(url + combineUrl + "14");
		String poiid = assertResult(result);
		if (poiid != null) {
			return poiid + ",140000";
		}
		result = HttpclientUtil.get(url + combineUrl + "08");

		poiid = assertResult(result);
		return poiid == null ? null : poiid + ",080000";
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
					return result.split(",")[1] + "," + result.split(",")[2] + "," + result.split(",")[3];
				}
			}
		}
		return null;
	}

	/**
	 * 拼装url访问串,地址和经纬度至少有一个有值，别的必须有值不能为空
	 *
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	private static String getHttpUrl(Map data)
			throws UnsupportedEncodingException {
		String name = null;
		if (data.get("name") != null) {
			name = URLEncoder.encode(data.get("name").toString(), "gbk");
		}
		if (name == null || name.equals("")) {
			return null;
		}
		String address = null;
		if (data.get("address") != null) {
			address = URLEncoder.encode(data.get("address").toString(), "gbk");
		}
		String tempurl = "cp=" + data.get("cp") + "&cpid=" + data.get("id")
				+ "&name=" + name + "&address=" + address;
		// 地址和经纬度必须有一个有值:支持数据中经纬度为lat、lng或x、y的情况
		if (data.containsKey("lat") && data.containsKey("lng")) {
			if (address == null
					&& (data.get("lat") == null || data.get("lng") == null)) {
				return null;
			}
			return tempurl + "&x=" + data.get("lng") + "&y=" + data.get("lat")
					+ "&big_type=";
		}
		if (address == null && (data.get("x") == null || data.get("y") == null)) {
			return null;
		}
		return tempurl + "&x=" + data.get("x") + "&y=" + data.get("y")
				+ "&big_type=";
	}

	/**
	 * 通过api读取数据时直接匹配写入对应的poiid：首先获取匹配的poiid，并且返回增加poiid后的map
	 *
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getMapContainsPoiid(Map data)
			throws UnsupportedEncodingException {
		data = mapToLower(data);
		String poiidType = getMatchPoiid(data);
		if (poiidType == null) {
			data.put("poiid", null);
			data.put("match_type", null);
			data.put("match_distance", null);
			data.put("type", null);
		} else {
			data.put("poiid", poiidType.split(",")[0]);
			data.put("match_type", poiidType.split(",")[1]);
			data.put("match_distance", poiidType.split(",")[2]);
			data.put("type", poiidType.split(",")[3]);
		}
		return data;
	}

	/**
	 * 转成小写
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map mapToLower(Map data) {
		Map map = new HashMap();
		for (Object o : data.keySet()) {
			String s = (String) o;
			String sl = s.toLowerCase();
			map.put(sl, data.get(o) + "");
		}
		data = map;
		return data;
	}
}
