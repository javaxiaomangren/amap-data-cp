/**
 * 2013-5-9
 */
package com.amap.data.save;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.amap.base.utils.DateUtil;

/**
 * 调用save接口前，处理数据用到的方法
 */
public class SaveHelper {
	// 设置日期格式,用于from中的update_time，每次都是获取系统当前时间
	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 根据传入的深度信息，封装from字段信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getFrom(Map result, String cp) {
		Object id = result.get("id");
		Object url = result.get("url");
		Object src_version = result.get("src_version");
		Object update_flag = result.get("update_flag");
		Map from = new LinkedHashMap();
		if("hotel_ctrip_wireless_api".equalsIgnoreCase(cp)){
			from.put("city", result.get("city") == null || result.get("city").equals("") || result.get("city").equals("null") ? "1" : result.get("city"));
		}
		from.put("opt_type", GetOptType(update_flag));

		//dianping来源的话，from中需要增加group_src_type
		if(result.keySet().contains("group_src_type")){
			from.put("group_src_type", result.get("group_src_type"));
		}
		// 获取update_time
		from.put("update_time", getUpdateTime());
		from.put("src_type", cp);
		// TODO:src_version如何取值
		from.put("src_version", src_version == null || src_version.equals("") ? "1" : src_version);
		from.put("src_id", id);
		from.put("src_url", url);
		return from;
	}
	
	/**
	 * 判断基础信息是否完备，完备的话返回true，否则返回false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean assertBase(LinkedHashMap baselink){
		if(baselink.keySet().size() > 25 && baselink.keySet().contains("checked")){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据传入的深度信息，封装from字段信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getFrom(Object id, Object url, Object update_flag, String cp) {
		Map from = new LinkedHashMap();
		from.put("opt_type", GetOptType(update_flag));

		// 获取update_time
		from.put("update_time", getUpdateTime());
		from.put("src_type", cp);
		// TODO:src_version如何取值
		from.put("src_version", "1");
		from.put("src_id", id);
		from.put("src_url", url);
		return from;
	}
	
	/**
	 * 来源信息中已经有from字段，直接组装即可
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object combineFrom(Map result,
			String cp) {
		Map from = new LinkedHashMap();
		from.put("opt_type", result.get("opt_type"));
		// 获取update_time
		from.put("update_time", getUpdateTime());
		from.put("src_type", cp);
		from.put("src_version", result.get("src_version"));
		from.put("src_id", result.get("src_id").toString());
		from.put("src_url", result.get("src_url"));
		return from;
	}

	/**
	 * 根据update_flag，获取opt_type标记：update_flag为2时代表数据删除；否则为修改标记
	 */
	private static String GetOptType(Object update_flag) {
		if ("2".equals(update_flag)) {
			return "d";
		}
		return "u";
	}

	/**
	 * 更新时间以当前的系统时间为准
	 */
	public static Object getUpdateTime() {
		return df.format(new Date());
	}
	
	/**
	 * 直接从网站上抓取的json串中可能包含\t等字符，需要进行替换
	 */
	public static String getTranserJson(Object json){
		String jsonStr = null;
		if(json != null){
			jsonStr = json.toString();
			jsonStr = jsonStr.replaceAll("\t", "\\\\t");
			jsonStr = jsonStr.replaceAll("\n", "\\\\n");
		}
		return jsonStr;
	}
	
	/**
	 * 转成小写
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map transferToSmall(Map m){
		Map result = new HashMap();
		for(Object key : m.keySet()){
			String keyStr = key.toString().toLowerCase();
			result.put(keyStr, m.get(key));
		}
		return result;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> transferToSmall(List<Map> ms){
		List<Map> results = new ArrayList<Map>();
		for(Map m : ms){
			Map result = new HashMap();
			for(Object key : m.keySet()){
				String keyStr = key.toString().toLowerCase();
				result.put(keyStr, m.get(key));
			}
			results.add(result);
		}
		return results;
	}
	
	/**
	 * 时间转成标准格式
	 */
	public static String getTimeFormat(String time, String fromFormat){
		String toFormat = "yyyy-MM-dd HH:mm:ss";
		return DateUtil.parseDate(time, fromFormat, toFormat);
	}
	
	/**
	 * 判断动态信息是否是空的:是的话 返回true
	 */
	@SuppressWarnings("rawtypes")
	protected static boolean assertRtiNull(Map rti){
		//有一个不为空则返回false
		for(Object key : rti.keySet()){
			if(key.equals("market")){
				continue;
			}
			if(rti.get(key) != null && !rti.get(key).equals("") && !rti.get(key).equals("[ ]") && !rti.get(key).equals("{ }")){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断深度信息是否是空的，是的话，返回true
	 */
	@SuppressWarnings("rawtypes")
	protected static boolean assertDeepNull(Map deep){
		//有一个不为空则返回false
		for(Object key : deep.keySet()){
			if(key.equals("business")){
				continue;
			}
			if(deep.get(key) != null && !deep.get(key).equals("") && !deep.get(key).equals("[ ]") && !deep.get(key).equals("{ }")){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 从cav文件中读取poiid
	 * 用于测试的poiid
	 * @throws java.io.IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static List<Map> getPoiidFromCsv() throws IOException{
		List<Map> poiid = new ArrayList<Map>();
		String fullDataPath = "E://Poiid.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullDataPath)));
		String data;
		while ((data = reader.readLine()) != null) {
			Map temp = new HashMap();
			temp.put("poiid", data);
			poiid.add(temp);
		}
		return poiid;
	}
}
