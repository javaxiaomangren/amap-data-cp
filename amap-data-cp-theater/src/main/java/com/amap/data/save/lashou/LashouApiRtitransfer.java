/**
 * 2013-5-23
 */
package com.amap.data.save.lashou;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.lashou.fieldMap.GroupTcodeTnameMap;
import com.amap.data.save.lashou.fieldMap.PicsMap;
import com.amap.data.save.transfer.ApiRtitransfer;
import com.amap.data.save.tuan800.fieldMap.DiscountMap;
import com.amap.data.save.tuan800.fieldMap.RtiTimeMap;
import com.amap.data.save.tuan800.fieldMap.ShopsMap;

public class LashouApiRtitransfer extends ApiRtitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new RtiTimeMap(templet));// 动态信息时间映射
		 l.add(new DiscountMap(templet));// 动态信息折扣映射
		 l.add(new GroupTcodeTnameMap(templet));// 动态信息映射
		 l.add(new PicsMap(templet));// 动态信息图片映射
		 l.add(new ShopsMap(templet));
		return l;
	}
}
