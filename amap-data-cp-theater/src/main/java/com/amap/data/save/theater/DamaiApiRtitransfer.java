/**
 * @caoxuena
 * 2013-4-3
 *DamaiApiRtitransfer.java
 */
package com.amap.data.save.theater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.save.theater.fieldMap.UrlPicMap;
import com.amap.data.save.transfer.ApiRtitransfer;

public class DamaiApiRtitransfer extends ApiRtitransfer {
	// 判断当前处理的动态信息是否过期，如果是的话 则不处理
	@SuppressWarnings("rawtypes")
	@Override
	protected boolean assertIsOverDate(Map rti) {
		if ("4".equals(rti.get("state"))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		l.add(new UrlPicMap(templet));//动态信息url映射
		return l;
	}

}
