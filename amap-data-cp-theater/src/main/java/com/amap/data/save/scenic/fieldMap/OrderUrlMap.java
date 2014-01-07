/**
 * 2013-9-2
 */
package com.amap.data.save.scenic.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class OrderUrlMap extends FieldMap {
	public OrderUrlMap(TempletConfig templet) {
		type = "跳转url映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Object order_url = from.get("order_url");
		if (order_url != null && !order_url.equals("")) {
			order_url = order_url + "&s=gaode";
		}

		to.put("order_url", order_url);
		return true;
	}
}
