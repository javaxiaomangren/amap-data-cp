/**
 * 2013-5-27
 */
package com.amap.data.save.ctripwireless;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.ctripwireless.fieldMap.InfoWeburlMap;
import com.amap.data.save.ctripwireless.fieldMap.PicsMap;
import com.amap.data.save.ctripwireless.fieldMap.PriceMap;
import com.amap.data.save.ctripwireless.fieldMap.SrcStarMap;
import com.amap.data.save.transfer.Apitransfer;

public class CtripWirelessApitransfer extends Apitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PicsMap(templet));// 深度图片态息映射
		 l.add(new PriceMap(templet));// 深度最低价格映射
		 l.add(new SrcStarMap(templet));// 深度最低价格映射
		 l.add(new InfoWeburlMap(templet));// info_weburl映射
		return l;
	}
}
