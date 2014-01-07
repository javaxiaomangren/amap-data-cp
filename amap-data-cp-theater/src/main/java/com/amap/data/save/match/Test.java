/**
 * 2013-11-7
 */
package com.amap.data.save.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.mongodb.util.JSON;

/**
 * 测试新的聚合函数是否好使
 */
public class Test {
	private static NewMatch newMatch = new NewMatch();
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public static void main(String[] args) throws Exception{
		String sql = "SELECT * FROM poi_deep limit 30";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> Datas = ddr.readList();
		
		List<String> datas = new ArrayList<String>();
		//处理成指定格式
		for(Map Data : Datas){
			Object cp = Data.get("cp");
			Object src_id = Data.get("id");
			
			Object deepObj = Data.get("deep");
			Map deep = (Map) JSON.parse(deepObj.toString());
			Object base = deep.get("base");
			
			Map from = new HashMap();
			from.put("src_type", cp);
			from.put("src_id", src_id);
			
			Map data = new HashMap();
			data.put("from", from);
			data.put("base", base);
			String temp = JSON.serialize(data);
			datas.add(temp);
		}
		
		//调用匹配接口
		for(String data : datas){
			String result = newMatch.getMatchPoiid(data, false);
			System.out.println(result);
		}
	}
}
