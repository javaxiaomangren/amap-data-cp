/**
 * 2013-4-26
 */
package com.amap.theater.match;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.data.base.TempletConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchAndUpdatePoiid {
	private static final Logger log = LoggerFactory.getLogger(MatchAndUpdatePoiid.class);
	/**
	 * 根据传入的表名，从中获取poiid为空的数据，进行匹配后更新对应的poiid和更新标记update_flag
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	public static void matchAndUpdatePoiid(TempletConfig templet) throws UnsupportedEncodingException {
		String table = templet.getString("to_table");
		String cp = templet.getString("poiid_map");
		// 从指定的table中获取数据
		List<Map> datas = getData(cp, table);
		log.info("总共需要参与匹配的数据有：" + datas.size() + "个");
		// 获取对应的poiid，如果poiid不为空则对应更新table中的信息
		updatetTable(datas, table, cp, templet);
	}

	/**
	 * 从指定的表中获取poiid为空的数据信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getData(String cp, String table) {
		String sql = "SELECT * FROM " + table
				+ " WHERE cp = '" + cp + "' and (poiid = 'null' or poiid is null or poiid = '')";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readAll();
	}

	/**
	 * 根据传入的数据，获取对应的poiid信息，如果poiid不为空，则对应更新table中的信息;
	 * 每100条执行一次更新语句
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings({ "rawtypes", "static-access" })
	private static void updatetTable(List<Map> datas, String table, String cp,
			TempletConfig templet) throws UnsupportedEncodingException {
		List<String> sqlList = new ArrayList<String>();
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		int total_num = 0;
		int count = 0;
		for (Map data : datas) {
			total_num ++;
			if(total_num % 100 == 0){
				log.info("已经参与匹配" + total_num + "个");
			}
			String poiidType = GetMatchPoiid.getMatchPoiid(data);
			if (poiidType != null && !poiidType.equals("") && !poiidType.startsWith("0")) {
				String poiid = poiidType.split(",")[0];
				String type = poiidType.split(",")[1];
				String sql = "update " + table + " set poiid = '" + poiid
						+ "', type = '" + type + "', update_flag = " + templet.UPDATE
						+ " where cp = '" + cp + "' and id = '" + data.get("id") + "'";
				count++;
				sqlList.add(sql);
				if(count % 100 == 0){
					dbexec.setSqlList(sqlList);
					dbexec.dbExec();
					sqlList = new ArrayList<String>();
					log.info("已经成功匹配" + count + "个");
				}
			}
		}
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();
		log.info("总共从表" + table + "中获取poiid为空的数据有：" + datas.size() + "条，其中能匹配上的共有：" + count + "条");
	}
}
