package com.amap.data.base.fieldmap;

import java.util.HashMap;
import java.util.Map;

import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class OptTypeMap extends FieldMap {
	private Map<String, String> colMap;
	private String type;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OptTypeMap(TempletConfig templet) {
		type = "opt_type映射";
		String col = templet.getString("opt_type_map");
		type = templet.getString("opt_type_gen");
		String t[] = col.split("-");
		colMap = new HashMap();
		colMap.put(t[1], t[0]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		for (String s : colMap.keySet()) {
			String tCol = colMap.get(s);
			String value = ObjectUtil.toString(from.get(s));

			if ("deep".equals(type)) {
				if (value.equals("0")) {
					to.put(tCol, "a");
				} else if (value.equals("1") || value.equals("3")) {
					to.put(tCol, "u");
				} else if (value.equals("2")) {
					to.put(tCol, "d");
				} else {
					errMessage = "增量标记不能正确对应";
					Map m = new HashMap();
					errValue = m;
					m.put(s, value);
					return false;
				}
			} else if ("rti".equals(type)) {
				if (value.equals("0")) {
					to.put(tCol, "a");
				} else if (value.equals("1")) {
					to.put(tCol, "d");
				} else {
					errMessage = "增量标记不能正确对应";
					Map m = new HashMap();
					errValue = m;
					m.put(s, value);
					return false;
				}
			}

		}
		return true;
	}
}
