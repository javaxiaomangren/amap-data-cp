/**
 * 2013-7-19
 */
package com.amap.theater.staticnum;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;

import java.util.List;
import java.util.Map;

/**
 * 统计大麦网匹配和未匹配上的动态信息总数（根据rtiid区分的）
 */
public class RtiStatic {
	@SuppressWarnings({ "rawtypes" })
	public static void main(String[] args){

		int count = 0;
		getMatchids();
		List<Map> ids = getids();
		count = getNum(ids);
		System.out.println("匹配上的动态信息总数为：" + count);
		
		getNotMatchids();
		ids = getids();
		count = getNum(ids);
		System.out.println("未匹配上的动态信息总数为：" + count);
		
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		String sql = "DROP TABLE theaterid_poiid";
		dbexec.setSql(sql);
		dbexec.dbExec();
	}
	
	//获取个数
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static int getNum(List<Map> ids){
		int count = 0;
		for(Map id : ids){
			String sql = "select * FROM theatre_rti WHERE state != 4 and id = '" + id.get("id") + "'";
			DBDataReader ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> result = ddr.readAll();
			count += result.size();
		}
		
		return count;
	}
	
	//get all id match
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map> getids(){
		String sql = "select id from theaterid_poiid";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readAll();
	}
	
	//选出表中未匹配上的所有id信息
	private static void getNotMatchids(){
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		String sql = "DROP TABLE theaterid_poiid";
		dbexec.setSql(sql);
		dbexec.dbExec();
		
		sql = "CREATE TABLE theaterid_poiid SELECT DISTINCT id FROM poi_deep WHERE cp = 'theater_damai_api' AND (poiid IS NULL OR poiid = '' OR poiid = 'null')";
		dbexec.setSql(sql);
		dbexec.dbExec();
	}
	
	// 选出表中未匹配上的所有id信息
	private static void getMatchids() {
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		String sql = "CREATE TABLE theaterid_poiid SELECT DISTINCT id FROM poi_deep WHERE cp = 'theater_damai_api' AND (poiid IS NOT NULL AND poiid != '' AND poiid != 'null')";
		dbexec.setSql(sql);
		dbexec.dbExec();
	}
}
