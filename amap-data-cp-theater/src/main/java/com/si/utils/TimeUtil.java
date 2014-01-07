package com.si.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	/**
	 * 格式化日期时间
	 * @param basicDate 要格式化的日期
	 * @param strFormat 返回字符串格式
	 * @return
	 */
	public static String formatDateTime(Date basicDate, String strFormat) {
		SimpleDateFormat df = new SimpleDateFormat(strFormat);
		return df.format(basicDate);
	}

	/**
	 * 根据字符串格式化日期
	 * @param basicDate 要格式化的日期字符串
	 * @param strFormat 返回的格式
	 * @return 返回格式化后的日期时间字符串
	 * @throws java.text.ParseException
	 */
	public static String formatDateTime(String basicDate, String strFormat) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(strFormat);
		Date tmpDate = df.parse(basicDate);
		return df.format(tmpDate);
	}

	/**
	 * 根据类型返回减n年/月/天/小时/分/秒/毫秒后的时间
	 * @param type 要减的天数
	 * @param n 要减的数
	 * @return 返回减n后的时间字符串
	 */
	public static String getNBeforeTime(String type,int n,String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar rightNow = Calendar.getInstance();
		System.out.println("减之前的时间："+df.format(rightNow.getTime()));
		if("year".equals(type)) {
			rightNow.add(Calendar.YEAR, -n);
		}else if("month".equals(type)) {
			rightNow.add(Calendar.MONTH, -n);
		}else if("day".equals(type)) {
			rightNow.add(Calendar.DAY_OF_MONTH, -n);
		}else if("hour".equals(type)) {
			rightNow.add(Calendar.HOUR_OF_DAY, -n);
		}else if("minute".equals(type)) {
			rightNow.add(Calendar.MINUTE, -n);
		}else if("second".equals(type)) {
			rightNow.add(Calendar.SECOND, -n);
		}
		return df.format(rightNow.getTime());
	}

	/**
	 * 根据类型返回加n年/月/天/小时/分/秒/毫秒后的时间
	 * @param type 要加的数量
	 * @param n 要加的数
	 * @return 返回加n后的时间字符串
	 */
	public static String getNAfterTime(String type,int n,String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar rightNow = Calendar.getInstance();
		System.out.println("加之前的时间："+df.format(rightNow.getTime()));
		if("year".equals(type)) {
			rightNow.add(Calendar.YEAR, +n);
		}else if("month".equals(type)) {
			rightNow.add(Calendar.MONTH, +n);
		}else if("day".equals(type)) {
			rightNow.add(Calendar.DAY_OF_MONTH, +n);
		}else if("hour".equals(type)) {
			rightNow.add(Calendar.HOUR_OF_DAY, +n);
		}else if("minute".equals(type)) {
			rightNow.add(Calendar.MINUTE, +n);
		}else if("second".equals(type)) {
			rightNow.add(Calendar.SECOND, +n);
		}

		return df.format(rightNow.getTime());
	}

	/**
	 * 在给定之间基础上减去n数量后的时间
	 * @param time 时间值
	 * @param type 操作字段
	 * @param n 减去的数量
	 * @param format 转换格式
	 * @return 减去n后的时间
	 * @throws java.text.ParseException
	 */
	public static String nBeforeTimeString(String time,String type,int n,String format) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.setTime(df.parse(time));
		if("year".equals(type)) {
			c.add(Calendar.YEAR, -n);
		}else if("month".equals(type)) {
			c.add(Calendar.MONTH, -n);
		}else if("day".equals(type)) {
			c.add(Calendar.DAY_OF_MONTH, -n);
		}else if("hour".equals(type)) {
			c.add(Calendar.HOUR_OF_DAY, -n);
		}else if("minute".equals(type)) {
			c.add(Calendar.MINUTE, -n);
		}else if("second".equals(type)) {
			c.add(Calendar.SECOND, -n);
		}
		return df.format(c.getTime());
	}
	
	

	public static void main(String[] args) {
		String time = getNBeforeTime("second",30,"yyyy-MM-dd HH:mm:ss");
		System.out.println("处理后的时间="+time);
		try {
			String one = nBeforeTimeString(time, "minute", 1, "yyyy-MM-dd HH:mm:ss");
			System.out.println("处理后的时间222="+one);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
