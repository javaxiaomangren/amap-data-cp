/**
 * @author caoxuena
 *2013-1-6
 */
package com.amap.data.base.fieldmap;

/**
 * @author caoxuena
 *2012-12-18
 */
import java.util.HashMap;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class HotelStarMap extends FieldMap {

	private String starMap;
	@SuppressWarnings("unused")
	private boolean isWarning;

	public HotelStarMap(TempletConfig templet) {
		starMap = templet.getString("star_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		// 遍历需要进行空字符串转换的各字段
		String ziduanName = starMap;
		Object o = from.get(ziduanName);

		String ziduanInfo = null;

		if (o != null) {
			ziduanInfo = o.toString();
			// 不在规定星级范围内
			if (assertStar(ziduanInfo)) {
				to.put(ziduanName, null);
				errValue = new HashMap();
				errValue.put(starMap, from.get(starMap));
				return false;
			}
		}
		return true;
	}

	public boolean assertStar(String star) {
		String[] stars = { "1", "2", "3", "4", "5" };

		for (int i = 0; i < stars.length; i++) {
			if (star.equals(stars[i])) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getType() {
		return "酒店星级不在规定范围内";
	}
}
