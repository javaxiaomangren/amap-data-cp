/**
 * 2013-5-27
 */
package com.amap.data.save.cinemamerge.fieldmap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class CouponMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(CouponMap.class);
	private String coupon;

	public CouponMap(TempletConfig templet) {
		type = "深度信息图片映射";
		coupon = templet.getString("coupon_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		try {
			List<Map> coupons = (List<Map>) from.get(coupon);
			for (Map m : coupons) {
				LinkedHashMap temp = new LinkedHashMap();
				for(Object key : m.keySet()){
					temp.put(key, m.get(key) == null || m.get(key).equals("") || m.get(key).equals("null") ? null : m.get(key));
				}
				temps.add(temp);
			}
		} catch (Exception e) {
			log.info("coupon映射错误！！！");
		}
		to.put(coupon, temps.size() == 0 ? null : temps);
		return true;
	}
}