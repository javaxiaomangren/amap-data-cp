/**
 * 2013-5-27
 */
package com.amap.data.save.hotelvp;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.ctripwireless.fieldMap.PicsMap;
import com.amap.data.save.transfer.Apitransfer;

public class HotelVpApitransfer extends Apitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PicsMap(templet));// 深度图片态息映射
		return l;
	}
}
