/**
 * 2013-5-13
 */
package com.amap.data.save.dianping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.save.dianping.fieldMap.GroupbuyListMap;
import com.amap.data.save.transfer.ApiRtitransfer;
import com.amap.data.save.tuan800.fieldMap.GroupTcodeMap;

public class DianpingApiRtitransfer extends ApiRtitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new GroupbuyListMap(templet));// 动态信息映射
		 l.add(new GroupTcodeMap(templet));// 动态信息映射
		return l;
	}

	// 获取内部封装的json：rti
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void combineRti() {
		if(tMap.get("rti") != null){
			rtiMaps.addAll((List<Map>) tMap.get("rti"));
		}
	}

}
