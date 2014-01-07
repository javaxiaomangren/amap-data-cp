/**
 * 2013-5-15
 */
package com.amap.data.save.diandian;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.data.save.Save;
import com.mongodb.util.JSON;

public class DiandianSave extends Save{
	@Override
	public void init(String cp) {
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LinkedHashMap combineSpec(LinkedHashMap combineMap,
			String cp) { 
		//取出对应的id
		Map from = (Map) combineMap.get("from");
		Object id = from.get("src_id");
		String sql = "select * from poi_deep where cp = '" + cp + "' and id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> deeps = ddr.readAll();
		Object deepObj = deeps.get(0).get("deep");
		Map info = (Map) JSON.parse(deepObj+"");
		Object deepinfo = info.get("deep");
		if(deepinfo == null || deepinfo.equals("")){
			return null;
		}
		Map deep = (Map) JSON.parse(deepinfo.toString());
		Object dish_url = deep.get("DISH_URL");
		Map temp = new HashMap();
		temp.put("dish_url", dish_url);
		
		Map diandain = new HashMap();
		diandain.put(cp, temp);
		
		combineMap.put("spec", diandain);
		return combineMap;
	}
}
