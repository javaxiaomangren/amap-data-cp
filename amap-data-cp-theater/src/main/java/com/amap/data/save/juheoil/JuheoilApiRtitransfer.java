/**
 * 2013-5-23
 */
package com.amap.data.save.juheoil;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.juheoil.fieldMap.PriceListMap;
import com.amap.data.save.transfer.ApiRtitransfer;

public class JuheoilApiRtitransfer extends ApiRtitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PriceListMap(templet));//油价信息映射处理
		return l;
	}
}
