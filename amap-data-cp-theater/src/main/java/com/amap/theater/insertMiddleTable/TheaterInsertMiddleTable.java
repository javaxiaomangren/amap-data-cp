/**
 * 2013-5-8
 */
package com.amap.theater.insertMiddleTable;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 大麦网数据入poi_deep和poi_rti
 */
public class TheaterInsertMiddleTable {
	private static final Logger log = LoggerFactory.getLogger(TheaterInsertMiddleTable.class);
	private static String cp = "theater_damai_api";
	private static String url = "http://10.2.134.64:8085/saveDeepRti/SaveDeepRti?";
	private static String deep_table = "theatre_damai";
	private static String rti_table = "theatre_rti";
	
	/**
	 * 深度信息入库
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void insertDeepTable(){
		//首先获取需要入库的数据
		List<Map> deepDatas = getDatas(deep_table);
		log.info("总共需要入中间表的深度个数为：" + deepDatas.size());
		
		deepInsert(deepDatas);
	}
	/**
	 * 深度信息入库操作
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void deepInsert(List<Map> deepDatas){
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		
		int count = 0;
		for(Map deepData : deepDatas){
			Map urlMap = new HashMap();
			urlMap.put("flag", "deep");
			urlMap.put("cp", cp);
			urlMap.put("cpid", deepData.get("id").toString());
			urlMap.put("poiid", deepData.get("poiid"));
			
			//处理base
			Object base = deepData.get("base");
			if(base != null && !base.equals("") && !base.equals("null")){
				LinkedHashMap baseMap = (LinkedHashMap) JSON.parse(base.toString());
//				if(baseMap.get("checked") != null && baseMap.get("checked").equals("6")){
//					log.info("当前数据的checked值为6，不往中间表推送");
//					log.info("当前数据是：" + base);
//					continue;
//				}
				deepData.put("base", baseMap);
			}
			urlMap.put("deep", JSON.serialize(deepData));
			
			String result = "";
			try {
				result = HttpclientUtil.post(url, urlMap, "UTF-8");
			} catch (Exception e) {
			}
			if(!"success".equalsIgnoreCase(result)){
				log.info("当前访问串异常，没有成功入库，数据是：" + JSON.serialize(deepData));
			}else{
				count ++;
				if(count % 100 == 0){
					log.info("已经成功入库" + count);
				}
				//成功入库的更新其标记为0
				String sql = "update " + deep_table
						+ " set update_flag = 0 where id = '" + deepData.get("id") + "'";
				dbexec.setSql(sql);
				dbexec.dbExec();
			}
		}
		log.info("共成功入库" + count);
	}
	/**
	 * 动态信息入库
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void inserRtiTable(){
		//首先获取需要入库的数据
		List<Map> rtiDatas = getDatas(rti_table);
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		int count = 0;
		log.info("总共需要入中间表的动态个数为：" + rtiDatas.size());
		for(Map rtiData : rtiDatas){
			if("0".equals(rtiData.get("update_flag"))){
				continue;
			}
			Object id = rtiData.get("id");
			//根据当前id，取出对应的所有rtiid,并且state标记为非删除
			List<Map> rtis = getRtis(id);
			Map urlMap = new HashMap();
			urlMap.put("flag", "rti");
			urlMap.put("cp", cp);
			urlMap.put("cpid", id);
			urlMap.put("rti", JSON.serialize(rtis));
			urlMap.put("update_flag", "1");
			
			String result = "";
			try {
				result = HttpclientUtil.post(url, urlMap, "UTF-8");
			} catch (Exception e) {
				
			}
			if(!"success".equalsIgnoreCase(result)){
				log.info("当前访问串异常，没有成功入库，访问串数据是：" + JSON.serialize(rtis));
			}else{
				count ++;
				if(count % 100 == 0){
					log.info("已经成功入库" + count);
				}
				//成功入库的更新其标记为0
				String sql = "update " + rti_table
						+ " set update_flag = 0 where id = '" + id + "'";
				dbexec.setSql(sql);
				dbexec.dbExec();
			}
		}
		log.info("共成功入库" + count + ";本次动态更新成功执行完毕！");
	}
	/**
	 * 根据当前id，取出对应的所有rtiid,并且state标记为非删除
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map> getRtis(Object id){
		String sql = "SELECT * FROM " + rti_table + " WHERE id = '" + id + "' and state != '" + 4 + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readAll();
	}
	/**
	 * 获取需要入库的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map> getDatas(String table) {
		String sql = "SELECT * FROM " + table + " WHERE update_flag = 1";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readAll();
	}
	
	public static void main(String[] args){
		insertDeepTable();
		inserRtiTable();
	}
}
