/**
 * 2013-12-16
 * 统一处理Object类型映射
 */
package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class ObjectMap extends FieldMap {
	private final static Logger log = LoggerFactory.getLogger(ObjectMap.class);
	private Map<String, String> objectMap = new HashMap<String, String>();

	@SuppressWarnings("rawtypes")
	public ObjectMap(TempletConfig templet) {
		type = "Object类型映射";

		List fm = templet.getList("object_map");
		for (Object o : fm) {
			String s = (String) o;
			String[] t = s.split("-");
			objectMap.put(t[0], t[1]);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		for (String s : objectMap.keySet()) {
			if (from.get(s) == null || from.get(s).equals("")) {
				to.put(s, null);
			} else {
				try {
					List<Map> objects = (List<Map>) from.get(s);
					List<LinkedHashMap> results = new ArrayList<LinkedHashMap>();
					for (Map object : objects) {
						LinkedHashMap result = new LinkedHashMap();
						for (Object key : object.keySet()) {
							result.put(key, object.get(key) == null
									|| object.get(key).equals("") ? null
									: object.get(key));
						}
						results.add(result);
					}
					to.put(s, results);
				} catch (Exception e) {
					try {
						Map object = (Map) from.get(s);
						LinkedHashMap result = new LinkedHashMap();
						for (Object key : object.keySet()) {
							result.put(key, object.get(key) == null
									|| object.get(key).equals("") ? null
									: object.get(key));
						}
						to.put(s, result);
					} catch (Exception e1) {
						log.info("当前字段内容既不是List<Map>也不是Map");
					}
				}
			}
		}
		return true;
	}
}
