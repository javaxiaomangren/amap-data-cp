package com.amap.data.base.fieldmap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.DateUtil;
import com.amap.base.utils.ObjectUtil;
import com.amap.base.utils.RegexUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class TimeMap extends FieldMap {
	private static String minute_time_regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$";
	private static String timeformat = "yyyy-MM-dd HH:mm:ss";

	private Map<String, String> map = new HashMap<String, String>();

	@SuppressWarnings("rawtypes")
	public TimeMap(TempletConfig templet) {
		type= "时间映射";
		List fm = templet.getList("time_map");
		for (Object o : fm) {
			String s = (String) o;
			String[] t = s.split("-");
			map.put(t[0], t[1]);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		for (String s : map.keySet()) {
			String time = ObjectUtil.toString(from.get(map.get(s)));
			if (RegexUtil.isMatch(minute_time_regex, time)) {
				time += ":00";
			}
			Date d = DateUtil.strParseDate(time, timeformat);
			if (d == null){
				errMessage="错误的时间格式";
				errValue.put(map.get(s), from.get(map.get(s)));
				errValue.put("timeformat", timeformat);
				return false;
			}
			else {
				to.put(s, d);
			}
		}
		return true;
	}
}
