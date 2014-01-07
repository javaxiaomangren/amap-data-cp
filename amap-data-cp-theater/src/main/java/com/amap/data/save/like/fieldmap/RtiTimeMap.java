/**
 * 2013-5-23
 */
package com.amap.data.save.like.fieldmap;

import java.util.Map;

import com.amap.base.utils.DateUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class RtiTimeMap extends FieldMap {
	private String fromFormat = "yyyy-MM-dd";
	private String toFormat = "yyyy-MM-dd HH:mm:ss";

	public RtiTimeMap(TempletConfig templet) {
		type = "时间映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		try{
			to.put("group_time_beg", DateUtil.parseDate(from.get("group_time_beg").toString(), fromFormat, toFormat));
			to.put("group_time_end", DateUtil.parseDate(from.get("group_time_end").toString(), toFormat, toFormat));
		}catch (Exception e) {
		}
		to.put("starttime", to.get("group_time_beg"));
		to.put("endtime", to.get("group_time_end"));
		return true;
	}
}