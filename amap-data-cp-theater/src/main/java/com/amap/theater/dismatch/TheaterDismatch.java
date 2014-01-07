/**
 * 2013-7-15
 */
package com.amap.theater.dismatch;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.data.WriteToDB;
import com.amap.base.http.HttpclientUtil;
import com.amap.theater.insertMiddleTable.TheaterInsertMiddleTable;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大麦网的错误匹配处理：从接口获取错误匹配信息，入错误表，并更新大麦深度表和中间表
 */
public class TheaterDismatch {
	private final static Logger log = LoggerFactory.getLogger(TheaterDismatch.class);
	@SuppressWarnings("rawtypes")
	private static List<Map> datas = new ArrayList<Map>();

	//获取错误匹配信息接口
	private static String urlString = "http://192.168.3.215/ugc/api/result/v1/getBadMatch?key=76CE0491-2513-4388-A964-8D988DE9F34B&src_type=theater_damai_api&size=1000";
	
	//入错误匹配表接口
	private static String errorUrl = "http://10.2.134.64:8085/saveDeepRti/SaveDeepRti";
	
	public static void main(String[] args) throws Exception{
		getDismatchDatas();
		saveInfos();
		insertDeep();
	}
	/**
	 * 深度表中有更新的数据，入中间表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void insertDeep(){
		String sql = "select * from theatre_damai where update_flag = 1";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> results = ddr.readAll();
		TheaterInsertMiddleTable.deepInsert(results);
	}
	/**
	 * 错误信息入错误表
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean saveInfos() throws Exception{
		if(datas == null || datas.size() == 0){
			log.info("当前没有错误匹配信息");
			return true;
		}
		String cp = "theater_damai_api";
		for(Map data : datas){
			if(!cp.equals(data.get("src_type"))){
				continue;
			}
			Object poiid = data.get("poiid");
			Object id = data.get("src_id");
//			Object suggestpoiid = data.get("suggestpoiid");
			//入错误表
			Map m = new HashMap();
			m.put("flag", "error");
			m.put("cp", cp);
			m.put("cpid", id);
			m.put("poiid", poiid);
			String errorResult = HttpclientUtil.post(errorUrl, m);
			if(!"success".equalsIgnoreCase(errorResult)){
				log.info("当前错误匹配信息没有正确入库，当前信息是：" + m);
			}
			
			//修改中间深度表结果
			modifyMiddleTable(id);
		}
		return true;
	}
	
	
	/**
	 * 根据传入的id，修改中间表的值
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean modifyMiddleTable(Object id){
		//先查找原来表中是否有这条数据
		String sql = "select * from theatre_damai where id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> results = ddr.readAll();
		if(results == null || results.size() == 0){
			log.info("中间表中没有该条数据，对应的id是：" + id);
			return false;
		}
		
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		sql = "update theatre_damai set poiid = '', dismatch_flag = 1, update_flag = 1 where id = '" + id + "'";
		dbexec.setSql(sql);
		dbexec.dbExec();
		return true;
	}
	/**
	 * 获取错误匹配信息,同时记录本次访问的最大id，把id存入大麦id表
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void getDismatchDatas(){
		String result = getDismathInfos();
		try{
			Map resultMap = (Map) JSON.parse(result);
			Object statusObj = resultMap.get("status");
			Object statusmsg = ((Map) JSON.parse(statusObj.toString())).get("statusmsg");
			if("success".equals(statusmsg)){
				//获取错误匹配的所有信息
				Object data = resultMap.get("data");
				datas = (List<Map>) JSON.parse(data.toString());
				if(datas.size() > 0){
					//获取maxid，并对应入库
					Map id = new HashMap();
					id.put("id", resultMap.get("maxid"));
					new WriteToDB().toDBSingle("theater_id", id);
				}
			}else{
				//获取错误匹配信息时出错
				log.info("获取错误匹配信息时出错");
			}
		}catch (Exception e) {
			log.info("获取错误匹配信息时出错，错误原因是：" + e);
		}
	}
	
	/**
	 * 调用接口，获取误匹配信息
	 */
	private static String getDismathInfos(){
		// 从id表中获取上次处理的最大id信息，从最大id开始处理
		String id = getId();
		String url = urlString + "&start=" + id;
		return HttpclientUtil.get(url);
	}
	/**
	 * 从id表中获取本次处理开始的id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String getId(){
		String idStr = "0";
		String sql = "SELECT id FROM theater_id GROUP BY updatetime DESC";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> ids = ddr.readAll();
		Map idMap = ids.get(0);
		Object id = idMap.get("id");
		if(id == null || id.equals("null")){
			idStr = "0";
		} else {
			int idInt = Integer.parseInt(id.toString()) + 1;
			idStr = idInt + "";
		}
		return idStr;
	}
}
