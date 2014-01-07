/**
 * 2013-10-30
 */
package com.amap.data.save.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

/**
 * 批量修改sql中的数据
 */
public class UpdateSqlDataPiliang {
	private static String url = "http://10.2.134.64:8085/saveDeepRti/SaveDeepRti?";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args){
		String sql = "SELECT * FROM poi_deep WHERE cp ='discount_dingding_api' AND deep LIKE '%锦江之星%' AND deep NOT LIKE '%status%-1%' AND poiid != ''";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> dataMap = ddr.readAll();
		String cp = "discount_dingding_api";
		for(Map data : dataMap){
			String id = data.get("id").toString();
			String poiid = data.get("poiid").toString();
			Object deepObj = data.get("deep");
			String deep = deepObj.toString();
			Map deepMap = (Map) JSON.parse(deep);
//			System.out.println(deep);
			if(deepMap.containsKey("status")){
				deepMap.put("status", "-1");
				deep = JSON.serialize(deepMap);
			} else if (deepMap.containsKey("base")){
				deep = deep.replace(",\"base\"", ",\"status\":\"-1\",\"base\"");
			} else {
				deep = deep.replace("\"ispos\":\"0\"", "\"ispos\":\"0\",\"status\":\"-1\"");
			}
//			System.out.println(deep);
			
			Map urlMap = new HashMap();
			urlMap.put("flag", "deep");
			urlMap.put("cp", cp);
			urlMap.put("cpid", id);
			urlMap.put("poiid", poiid);
			urlMap.put("deep", deep);
			String result = "";
			try {
				result = HttpclientUtil.post(url, urlMap, "UTF-8");
			} catch (Exception e) {
				
			}
			if(!result.contains("success")){
				System.out.println(result + "," + id);
			}
		}
	}
}
