/**
 * 2013-6-4
 */
package com.amap.data.save.ctripwireless.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class PriceMap extends FieldMap {
	
	private String price;
	public PriceMap(TempletConfig templet) {
		type = "最低价格映射";
		price = templet.getString("price_lowest_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");
		
		Object price_lowest = null;
		if(deep != null && deep.get(price) != null && !deep.get(price).equals("")){
			String priceStr = deep.get(price).toString();
			if(priceStr.contains(".")){
				priceStr = priceStr.substring(0, priceStr.indexOf("."));
				price_lowest = priceStr;
			}else{
				price_lowest = priceStr;
			}
		}
		 to.put("price_lowest", price_lowest);
		return true;
	}
}