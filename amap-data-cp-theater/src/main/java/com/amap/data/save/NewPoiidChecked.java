/**
 * 2013-10-11
 */
package com.amap.data.save;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.util.JSON;

/**
 * 新增数据上线需要进行非空字段检查：如果有指定字段为空，则不上线
 */
public class NewPoiidChecked {
	private static List<String> ziduanFields = new ArrayList<String>();
	
	static{
		ziduanFields.add("poiid");
		ziduanFields.add("name");
		ziduanFields.add("new_type");
		ziduanFields.add("new_keytype");
		ziduanFields.add("x");
		ziduanFields.add("y");
		ziduanFields.add("code");
		ziduanFields.add("checked");
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean checkedNotNull(String combineJson){
		try{
			Map combine = (Map) JSON.parse(combineJson);
			Object baseObj = combine.get("base");
			if(baseObj == null || "".equals(baseObj)){
				return false;
			}
			
			Map base = (Map) JSON.parse(baseObj.toString());
			
			for(String field : ziduanFields){
				Object content = base.get(field);
				if(content == null || " ".equals(content)){
					return false;
				}
				
				//new_type判断位数
				if("new_type".equals(field)){
					if(content.toString().length() != 6){
						return false;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
