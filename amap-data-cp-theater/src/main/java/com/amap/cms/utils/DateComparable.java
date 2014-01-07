package com.amap.cms.utils;

import com.amap.base.utils.ObjectUtil;
import com.mongodb.DBObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class DateComparable  implements Comparator<DBObject>{
	public int compare(DBObject f1,DBObject f2) {
		String f1time = ObjectUtil.toString(((DBObject)f1.get("from")).get("update_time"));
		String f2time =  ObjectUtil.toString(((DBObject)f2.get("from")).get("update_time"));
		Date f1date = isDate(f1time);
		Date f2date = isDate(f2time);
		int tmp = 0;
		if(f1date!=null && f2date!=null) {
			if(f1date.after(f2date)) {
				tmp = 1;
			}else if(f1date.before(f2date)) {
				tmp = -1;
			}else {
				tmp = 0;
			}
		}
		return tmp;
	}
	
	
	/**
	 * 根据目录名称判断是否为日期
	 * @param sdf 格式化日期
	 * @param str 目录名称
	 * @return 是否为日期
	 */
	private Date isDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			sdf.setLenient(false);
			
			return sdf.parse(str);
		} catch (ParseException e) {
			//log.error(e.getMessage());
			return null;
		}
	}
}
