/**
 * 2013-5-27
 */
package com.amap.data.save.ctripwireless;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.ctripwireless.fieldMap.ReviewListMap;
import com.amap.data.save.transfer.ApiRtitransfer;

public class CtripWirelessApiRtitransfer extends ApiRtitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new ReviewListMap(templet));// 动态信息映射
		return l;
	}
}
