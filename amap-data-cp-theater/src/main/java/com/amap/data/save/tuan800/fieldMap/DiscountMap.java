/**
 * 2013-5-24
 */
package com.amap.data.save.tuan800.fieldMap;

import java.text.DecimalFormat;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class DiscountMap extends FieldMap {
	private DecimalFormat df = new DecimalFormat("0.0");

	public DiscountMap(TempletConfig templet) {
		type = "折扣映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Object group_price_ori = from
				.get("group_price_ori");
		Object group_price = from.get("group_price");
		String discount = "0.0";
		if (group_price_ori != null && group_price != null) {
			to.put("group_price", group_price.toString());
			try {
				discount = df
						.format((Double.parseDouble(group_price
								.toString()) / Double
								.parseDouble(group_price_ori
										.toString())) * 10);
			} catch (Exception e) {
			}
		}
		to.put("group_discount", discount);
		to.put("market", "groupbuy");
		return true;
	}
}