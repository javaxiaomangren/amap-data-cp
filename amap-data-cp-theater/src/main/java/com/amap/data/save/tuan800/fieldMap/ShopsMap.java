/**
 * 2013-5-24
 */
package com.amap.data.save.tuan800.fieldMap;

import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.mongodb.util.JSON;

public class ShopsMap extends FieldMap {
	public ShopsMap(TempletConfig templet) {
		type = "shops映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Object shops = from
				.get("shops");
		List<Map> shopsMap = null;
		if (shops != null) {
			shopsMap =  (List<Map>) JSON.parse(shops.toString());
		}
		to.put("shops", shopsMap);
		return true;
	}
}