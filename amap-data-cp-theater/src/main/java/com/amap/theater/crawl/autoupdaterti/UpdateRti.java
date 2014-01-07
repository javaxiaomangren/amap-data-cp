/**
 * 2013-6-27
 */
package com.amap.theater.crawl.autoupdaterti;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBDataWriter;
import com.amap.base.data.DBExec;
import com.amap.base.utils.DateUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 人为判断数据是否过期，并且判断是否是测试数据，如果是测试数据，也置为过期数据，保证其不上线
 */
public class UpdateRti {
	private static final Logger log = LoggerFactory.getLogger(UpdateRti.class);
	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void updateRti(){
		List<Map> addList = new ArrayList();
		List<String> sqlList = new ArrayList();

		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		
		//首先从表中读取状态不是4的所有动态数据
		String sql = "select * from theatre_rti where state != '4'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rtis = ddr.readAll();
		
		for(Map rti : rtis){
			boolean flag = assertValid(rti);
			if(flag){
				//过期数据，更新其标记位
				Object rtiid = rti.get("rtiid");
				Object id = rti.get("id");
				sql = "delete from theatre_rti where rtiid = '" + rtiid + "' and id = '" + id + "'";
				sqlList.add(sql);
				
				Map temp = new HashMap();
				temp.put("id", id);
				temp.put("rtiid", rtiid);
				temp.put("performinfo", rti.get("performinfo"));
				temp.put("state", "4");
				temp.put("update_flag", "1");
				addList.add(temp);
			}
		}
		
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();
		
		log.info("处理过期下线信息，共下线" + addList.size() + "条动态数据");
		sql = "insert into theatre_rti (id,rtiid,performinfo,state,update_flag)"
				+ "values(:id,:rtiid,:performinfo,:state,:update_flag)";
		DBDataWriter ddw = new DBDataWriter(sql);
		ddw.setDbenv(null);
		ddw.writeList(addList);
	}
	
	/**
	 * 判断当前信息是否有效：是否过期，或者是否是测试数据
	 */
	@SuppressWarnings("rawtypes")
	private static boolean assertValid(Map rti){
		//判断是否是过期数据
		if(assertOverDate(rti)){
			return true;
		}
		//判断是否是测试数据
		return assertTestDate(rti);
	}
	/**
	 * 判断是否是测试数据:名称中包含测试字段或者状态标记位为10
	 */
	@SuppressWarnings("rawtypes")
	private static boolean assertTestDate(Map rti){
		//如果标记位是10，返回true
		Map perforMap = (Map) JSON.parse(rti.get("performinfo").toString());
		Object status = perforMap.get("s");
		if(status != null && (status.equals("10") || status.equals("6"))){
			return true;
		}
		
		//判断名称中是否包含“测试”字段
		Object name = perforMap.get("n");
		if(name != null && name.toString().contains("测试")){
			return true;
		}
		
		//判断场馆名称中是否包含“测试”字段
		Object vname = perforMap.get("v");
		if(vname != null && vname.toString().contains("测试")){
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是过期动态信息
	 */
	@SuppressWarnings("rawtypes")
	private static boolean assertOverDate(Map rti){
		Map perforMap = (Map) JSON.parse(rti.get("performinfo").toString());
		Object timeObj = perforMap.get("t");
		if(timeObj == null || timeObj.equals("")){
			log.info("当前演出信息的时间是空的，无法判断，当前演出信息是：" + JSON.serialize(rti));
			return false;
		}
		String timeStr = timeObj.toString();
		if(timeStr.contains("..")){
			timeStr = timeStr.replace("..", ".");
		}
		if(timeStr.contains("--")){
			timeStr = timeStr.replace("--", "-");
		}
		String time = null;
		String regEx = "\\d{4}(.|-)\\d{1,}(.|-)\\d{1,}";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(timeObj.toString());
		while (m.find()) {
			time = m.group();
		}
		
		//针对2013年6月19日，提取出其中的日期
		regEx = "\\d{4}(年)\\d{1,}(月)\\d{1,}(日)";
		p = Pattern.compile(regEx);
		m = p.matcher(timeObj.toString());
		while (m.find()) {
			time = m.group();
			time = time.replace("年", "-");
			time = time.replace("月", "-");
			time = time.replace("日", "");
		}
		
		return assertTime(time);
	}
	
	//过期返回true，否则返回false
	private static boolean assertTime(String time){
		if(time == null || time.equals("") || time.equalsIgnoreCase("null")){
			return false;
		}
		
		try{
			if(time.contains("-") && time.contains(".")){
				if( DateUtil.parseDate(time, "yyyy-MM.dd", "yyyy-MM-dd HH:mm:ss") == null){
					time = DateUtil.parseDate(time, "yyyy.MM-dd", "yyyy-MM-dd HH:mm:ss");
				} else {
					time = DateUtil.parseDate(time, "yyyy-MM.dd", "yyyy-MM-dd HH:mm:ss");
				}
			}
			
			if(time.contains("-")){
				time = DateUtil.parseDate(time, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss");
			}
			
			if(time.contains(".")){
				time = DateUtil.parseDate(time, "yyyy.MM.dd", "yyyy-MM-dd HH:mm:ss");
			}
			
			if(time.compareToIgnoreCase(df.format(new Date())) < 0){
				return true;
			}
		}catch (Exception e) {
			
		}
		
		return false;
	}
	
	public static void main(String[] args){
		updateRti();
	}
}
