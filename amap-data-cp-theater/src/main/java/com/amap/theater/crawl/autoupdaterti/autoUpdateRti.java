/**
 * 2013-5-9
 */
package com.amap.theater.crawl.autoupdaterti;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.data.WriteToDB;
import com.amap.data.base.TempletConfig;
import com.amap.theater.crawl.Crawl;
import com.amap.theater.insertMiddleTable.TheaterInsertMiddleTable;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class autoUpdateRti {
	private static final Logger log = LoggerFactory
			.getLogger(autoUpdateRti.class);

	@SuppressWarnings("rawtypes")
	public void run() {
		// 读取全量动态信息
		String s = "theater_damai_api_rti_1";
		TempletConfig templet = new TempletConfig(s);
		List<Map> dataList = new Crawl(templet).getAllRtis();

		// 保存数据，同时把没有更新的数据置为删除状态
		saveData(dataList);
		UpdateRti.updateRti();
		TheaterInsertMiddleTable.inserRtiTable();
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		// 读取全量动态信息
		String s = "theater_damai_api_rti_1";
		TempletConfig templet = new TempletConfig(s);
		List<Map> dataList = new Crawl(templet).getAllRtis();

		// 保存数据，同时把没有更新的数据置为删除状态
		saveData(dataList);
		UpdateRti.updateRti();
		TheaterInsertMiddleTable.inserRtiTable();
	}

	/**
	 * 保存数据：先把数据对应存入中间表，然后再写入deep表
	 */
	@SuppressWarnings("rawtypes")
	private static void saveData(List<Map> dataList) {
		// 数据存入中间表：包括更新和新增
		updateExisRtis(dataList);

		// 获取已经下线的动态信息：中间表中的标记位为4
		List<Map> offDatas = getOffRtis();

		// 把更新、新增、下线动态信息都写入中间表
		TheaterInsertMiddleTable.inserRtiTable();

		// 把表中过期的动态数据删除，防止下次重复统计
		deleteOverData(offDatas);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void updateExisRtis(List<Map> dataList) {
		// 首先从接口读取最新的场馆信息
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		// 判断读取的静态数据在深度表中是否存在
		List<String> sqlList = new ArrayList<String>();
		List<Map> addList = new ArrayList();
		for (Map rti : dataList) {
			Object id = rti.get("id");
			Object rtiid = rti.get("rtiid");
			String sql0 = "select * from theatre_rti where id = '" + id
					+ "' and rtiid = '" + rtiid + "'";
			DBDataReader ddr = new DBDataReader(sql0);
			ddr.setDbenv(null);
			List<Map> datas = ddr.readList();
			String sql = null;
			if (datas.size() == 1) {
				// 原表中有该条poi，进行更新,先删除原来数据，再插入
				sql = "delete from theatre_rti where id = '" + id
						+ "' and rtiid = '" + rtiid + "'";
				sqlList.add(sql);
				addList.add(rti);
			} else if (datas.size() == 0) {
				// 新增信息
				addList.add(rti);
			} else {
				// 报错，不可能找到多条
				log.error("有两条同一id的深度数据，错误！！！" + "id is : " + id);
				continue;
			}
		}
		log.info("需要更新的动态信息个数为：" + sqlList.size());
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();

		for (Map data : dataList) {
			try {
				new WriteToDB().toDBSingle("theatre_rti", data);
			} catch (Exception e) {
				System.out.println(JSON.serialize(data));
			}
		}
	}

	/**
	 * 获取已经下线的动态信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getOffRtis() {
		String sql = "select * from theatre_rti where update_flag = 0 and state != '4'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> offDatas = ddr.readAll();

		List<Map> datas = new ArrayList<Map>();
		for (Map data : offDatas) {
			data.put("status", "4");
			datas.add(data);
		}
		return datas;
	}

	/**
	 * 删除动态表中的过期数据
	 */
	@SuppressWarnings("rawtypes")
	private static void deleteOverData(List<Map> offDatas) {
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		List<String> sqlList = new ArrayList<String>();

		for (Map offdata : offDatas) {
			Object id = offdata.get("id");
			Object rtiid = offdata.get("rtiid");
			String sql = "delete from theatre_rti where id = '" + id
					+ "' and rtiid = '" + rtiid + "'";
			sqlList.add(sql);
		}
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();
	}
}
