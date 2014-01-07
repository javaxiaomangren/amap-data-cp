/**
 * 2013-5-16
 */
package com.amap.data.save.car.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class NewTypeMap extends FieldMap {
	private String new_type;
	public NewTypeMap(TempletConfig templet) {
		type = "类型映射";
		new_type = templet.getString("new_type_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map base = (Map) from.get(new_type);
		Object newType = base.get("new_type");
		String type = null;
		if(newType != null){
			type = newType.toString();
			int length = type.length();
			while(length < 6){
				type += "0";
				length = type.length();;
			}
		}
		 to.put("new_type", type);
		return true;
	}
}

