/**
 * @author caoxuena
 *2012-12-19
 */
package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.DateUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class TimeFormatMap extends FieldMap {

	private String toFormat = "yyyy-MM-dd HH:mm:ss";
	private List<String> timeNameMap = new ArrayList<String>();
	private List<String> timeFormatMap = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public TimeFormatMap(TempletConfig templet) {
		type= "时间格式映射";
		timeNameMap = templet.getList("time_name_map");
		timeFormatMap = templet.getList("time_format_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		for(int i = 0; i < timeFormatMap.size(); i++){
			String ziduanName = timeNameMap.get(i);
			String ziduanFormat = timeFormatMap.get(i);
			
			Object timeObj = from.get(ziduanName);
			String time = null;
			if(timeObj != null){
				time = DateUtil.parseDate(timeObj.toString(), ziduanFormat, toFormat);
			}
			
			to.put(ziduanName, time);
		}
		return true;
	}
	
	public static void main(String[] args){
		String time = "2013-07-25T00:00:00";
		String fromFormat = "yyyy-MM-dd";
		String toFormat = "yyyy-MM-dd HH:mm:ss";
		
		String d = DateUtil.parseDate(time, fromFormat, toFormat);
		System.out.println(d);
	}
}
