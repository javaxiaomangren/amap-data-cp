/**
 * @caoxuena
 * 2013-4-8
 *PoiDeepInfoInterface.java
 *根据cp和cpid往poi_deepinfo和poi_rti中写数据：已经存在的 直接update；没有的直接插入
 */
package com.amap.theater.tableInterface;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.data.WriteToDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 根据cp和cpid往poi_deepinfo和poi_rti中写数据：已经存在的 直接update；没有的直接插入
 */
public class PoiDeepRtiInfoInterface {
	protected static final Logger log = LoggerFactory.getLogger(PoiDeepRtiInfoInterface.class);
	
	private static String deepTable = "poi_deepinfo";
	private static String rtiTable = "poi_rti";
	private static String[] deepfields = {"cp","poiid","id","name","address","telephone","type","x","y","url","base","deep","update_flag"};
	private static String[] rtifields = {"cp","id","rti","update_flag"};
	private static WriteToDB writeToDB = new WriteToDB();
	/**
	 * 根据cp和cpid往poi_deepinfo中写数据：已经存在的 直接update；没有的直接插入；
	 * data中需要包含如下字段：cp、poiid、id、name、address、telephone、type、x、y、url、base、deep和update_flag；
	 * 其中base是规定格式的59个字段封装成的json格式；
	 * deep是按照所属的cp类型规格字段封装成的json格式
	 */
	@SuppressWarnings("rawtypes")
	public static void WriteToPoiDeepInfo(String cp, String id, Map data){
		//先判断传入的map是否包含所有字段
		assertContainsAllfields(data, deepfields);
		if(assertExist(cp, id, deepTable)){
			//存在，则先把原来的信息删除，然后直接写入
			deleteFromTable(cp, id, deepTable);
		}
		
		writeToDB.toDBSingle(deepTable, data);
	}
	
	/**
	 * 根据cp和cpid往poi_rti中写数据：已经存在的 直接update；没有的直接插入；
	 * data中需要包含如下字段：cp、id、rti和update_flag；
	 * rti是按照所属的cp类型规格字段封装成的json格式（同一id下的所有动态信息封装成一个数组json）
	 */
	@SuppressWarnings("rawtypes")
	public static void WriteToPoiRtiInfo(String cp, String id, Map data){
		//先判断传入的map是否包含所有字段
		assertContainsAllfields(data, rtifields);
		if(assertExist(cp, id, rtiTable)){
			//存在，则先把原来的信息删除，然后直接写入
			deleteFromTable(cp, id, rtiTable);
		}
		writeToDB.toDBSingle(rtiTable, data);
	}
	
	/**
	 * 判断传入的data中是否包含指定的字段信息，包括不全的话，报错	
	 */
	@SuppressWarnings("rawtypes")
	private static void assertContainsAllfields(Map data, String[] fields){
		Set ketSets = data.keySet();
		for(String field : fields){
			if(!ketSets.contains(field)){
				log.info("传入的Map中没有包含字段 ： " + field + ",传入的Map是： " + data);
				System.exit(0);
			}
		}
	}
	/**
	 * 根据cp和cpid判断要存入的目的表中是否已经存在该数据
	 */
	@SuppressWarnings("rawtypes")
	private static boolean assertExist(String cp, String id, String table){
		String sql = "SELECT * FROM " + table + " WHERE cp = '" + cp + "' AND id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		if( ((Map)ddr.readSingle()).size() > 0){
			return true;
		}
		
		return false;
	}
	
	private static void deleteFromTable(String cp, String id, String table){
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		String sql = "delete FROM " + table + " WHERE cp = '" + cp + "' AND id = '" + id + "'";
		dbexec.setSql(sql);
		dbexec.dbExec();
	}
}
