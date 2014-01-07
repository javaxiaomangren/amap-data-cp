/**
 * 2013-8-1
 */
package com.amap.data.save.dianping.fieldMap;

import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class DeepMap extends FieldMap {
	private List<String> deep_cols_map;

	@SuppressWarnings("unchecked")
	public DeepMap(TempletConfig templet) {
		type = "深度信息映射";
		deep_cols_map = templet.getList("deep_cols_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");
		for(String s : deep_cols_map){
			Object value = deep.get(s);
			if(value != null){
				value = value + "";
			}
			if(value == null || value.equals("") || value.equals("-1")){
				value = null;
			}
			if(value != null && value.equals("0.0")){
				value = "0";
			}
			to.put(s, value);
		}
		return true;
	}
}
