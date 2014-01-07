/**
 * 2013-10-22
 */
package com.amap.data.save.juheoil.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;

public class PriceListMap  extends FieldMap {
	private List<String> keys;
	
	@SuppressWarnings("unchecked")
	public PriceListMap(TempletConfig templet) {
		type = "油价信息映射";
		keys = templet.getList("price_list_cols");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> courses = new ArrayList<LinkedHashMap>();
		if(from.get("price_list") != null){
			List<Map> temps = (List<Map>) from.get("price_list");
			
			for(Map temp : temps){
				temp = SaveHelper.transferToSmall(temp);
				LinkedHashMap price = new LinkedHashMap();
				
				for(String key : keys){
					price.put(key, temp.get(key) == null || temp.get(key).equals("") ? null : temp.get(key));
				}
				courses.add(price);
			}
		}
		 to.put("price_list", courses);
		return true;
	}
}
