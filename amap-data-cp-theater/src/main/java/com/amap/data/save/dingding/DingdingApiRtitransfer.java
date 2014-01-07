/**
 * 2013-5-13
 */
package com.amap.data.save.dingding;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.dingding.fieldMap.PicsMap;
import com.amap.data.save.transfer.ApiRtitransfer;

public class DingdingApiRtitransfer extends ApiRtitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PicsMap(templet));// 动态信息图片映射
		return l;
	}
}
