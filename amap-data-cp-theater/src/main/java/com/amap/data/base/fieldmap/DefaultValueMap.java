package com.amap.data.base.fieldmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultValueMap extends FieldMap {

	private Map<String, String> map = new HashMap<String, String>();

	public DefaultValueMap(TempletConfig templet) {
		type= "缺省值设置";
		
		List fm = templet.getList("defaultvalue_map");
		for (Object o : fm) {
			String s = (String) o;
			String[] t = s.split("-");
			map.put(t[0], t[1]);
		}
	}

	@Override
	public boolean fieldmap(Map from, Map to) {
		for (String s : map.keySet()) {
			to.put(s, map.get(s));
		}
		return true;
	}
}
