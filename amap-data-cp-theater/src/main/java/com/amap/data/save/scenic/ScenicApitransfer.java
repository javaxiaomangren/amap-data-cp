/**
 * 2013-5-27
 */
package com.amap.data.save.scenic;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.scenic.fieldMap.OrderUrlMap;
import com.amap.data.save.scenic.fieldMap.PicsMap;
import com.amap.data.save.transfer.Apitransfer;

public class ScenicApitransfer extends Apitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PicsMap(templet));// 深度图片态息映射
		 l.add(new OrderUrlMap(templet));
		return l;
	}
}
