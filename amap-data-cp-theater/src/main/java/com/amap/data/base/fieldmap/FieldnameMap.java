package com.amap.data.base.fieldmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class FieldnameMap extends FieldMap {
	private Map<String, String> nameMap = new HashMap<String, String>();

	@SuppressWarnings("rawtypes")
	public FieldnameMap(TempletConfig templet) {
		type= "名称映射";
		
		List fm = templet.getList("field_map");
		for (Object o : fm) {
			String s = (String) o;
			String[] t = s.split("-");
			nameMap.put(t[0], t[1]);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		for (String s : nameMap.keySet()) {
			if(from.get(nameMap.get(s)) == null || from.get(nameMap.get(s)).equals("") || from.get(nameMap.get(s)).equals("[]")){
				to.put(s, null);
			}else{
				to.put(s, from.get(nameMap.get(s)));
			}
		}
		return true;
	}

}
