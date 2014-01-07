/**
 * 2013-5-27
 */
package com.amap.data.save.golf;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.golf.fieldMap.CourseMap;
import com.amap.data.save.golf.fieldMap.PicsMap;
import com.amap.data.save.transfer.Apitransfer;

public class GolfApitransfer extends Apitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PicsMap(templet));// 深度图片态息映射
		 l.add(new CourseMap(templet));
		return l;
	}
}
