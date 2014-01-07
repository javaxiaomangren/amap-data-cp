/**
 * 2013-10-30
 */
package com.amap.data.save.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;

/**
 * 手动修改sql中的一条数据
 */
public class UpdateSqlData {
	private static String url = "http://10.2.134.64:8085/saveDeepRti/SaveDeepRti?";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args){
		String cp = "ali_qqfood";
		String id = "2847242476509409395";
		String poiid = "B021412E5W";
		String sql = "SELECT * FROM poi_deep WHERE id = '" + id + "' AND cp = '" + cp + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> dataMap = ddr.readAll();
		Object deepObj = dataMap.get(0).get("deep");
		String deep = deepObj.toString();
		System.out.println(deep);
		deep = deep.replace("{\"poiid\":null,", "{\"poiid\":\"B021412E5W\",");
		System.out.println(deep);
		
		Map urlMap = new HashMap();
		urlMap.put("flag", "deep");
		urlMap.put("cp", cp);
		urlMap.put("cpid", "test");
		urlMap.put("poiid", poiid);
		urlMap.put("deep", deep);
		urlMap.put("update_flag", "1");
		String result = "";
		try {
			result = HttpclientUtil.post(url, urlMap, "UTF-8");
		} catch (Exception e) {
			
		}
		
		System.out.println(result);
	}
}
