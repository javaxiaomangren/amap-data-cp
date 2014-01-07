/**
 * 2013-5-14
 */
package com.amap.data.save.theater.fieldMap;

import java.util.Map;

import com.amap.base.utils.JsonUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;

public class RtiPriceLowestMap extends FieldMap {
	private String price;
	public RtiPriceLowestMap(TempletConfig templet) {
		type = "动态信息最低价格映射";
		price = templet.getString("price_lowest_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		String price_lowest = null;
		
		Object performObj = from.get(price);
		if(performObj != null){
			Map perform =JsonUtil.parseMap(SaveHelper.getTranserJson(performObj));
			Object priceObj = perform.get("p");
			if(priceObj != null){
				String prices = priceObj.toString();
				if(prices.contains(",")){
					price_lowest = prices.split(",")[0];
				}
			}
		}
		 to.put("price_lowest", price_lowest);
		return true;
	}
}
