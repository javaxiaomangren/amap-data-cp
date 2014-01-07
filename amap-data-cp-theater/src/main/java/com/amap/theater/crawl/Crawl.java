package com.amap.theater.crawl;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBDataWriter;
import com.amap.base.data.DBExec;
import com.amap.base.data.WriteToDB;
import com.amap.base.utils.HttpClientUtil;
import com.amap.base.utils.JsonUtil;
import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.TempletConfig;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class Crawl {
	@SuppressWarnings("rawtypes")
	protected List<Map> dataList;
	@SuppressWarnings("rawtypes")
	protected List<Map> rtilList;
	protected TempletConfig templet;

	private static final Logger log = LoggerFactory.getLogger(Crawl.class);
	private static WatchData watchData = new WatchData();

	public Crawl(TempletConfig templet) {
		this.templet = templet;
	}

	// 通过配置文件及id获得对应项目的url信息（不同cp来源的url格式可能不同）
	protected String getUrl(String hrl, Object id) {
		return hrl + id + ".html";
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public List<Map> getDeeps() throws IOException {
		dataList = new ArrayList();
		// 获取编码
		String charset = templet.getString("api_charset");
		HttpClientUtil hcu = new HttpClientUtil();
		String hrl = templet.getString("api_url_h5");
		String cityUrl = templet.getString("api_url_city");
		String Url = templet.getString("api_url_theater");
		String cityStr = hcu.httpGetReq(cityUrl, null, charset);
		String ziquanzong = "{\"citys\":" + cityStr;
		String last = ziquanzong + "}";
		Map cityMap = JsonUtil.parseMap(last);
		List<Map> cityList = (List) cityMap.get("citys");

		log.info("开始从api获取数据，需要获取" + cityList.size() + "个城市的数据");

		// 每个城市
		for (Map m : cityList) {
			String cityId = ObjectUtil.toString(m.get("i"));
			for (int i = 1; i <= 100; i++) {
				String Str = hcu.httpGetReq(Url + cityId + "&p=" + i, null,
						charset);
				Map theater = JsonUtil.parseMap(Str);
				List<Map> list = (List) theater.get("v");
				if (list != null) {
					for (Map n : list) {
						n.put("update_flag", templet.UPDATE);
						n.put("id", n.get("VenueID"));
						n.put("url", getUrl(hrl, n.get("VenueID")));
						n.put("poiid", null);
						n.put("type", null);
						n.put("match_type", null);
						n.put("match_distance", null);
						n.put("dismatch_flag", 0);
						n.put("base", watchData.getBase(n));
						dataList.add(n);
					}
				}

				if ((list == null || list.size() == 0)) {
					i = 101;
				}
			}
		}
		// 获取城市api
		log.info("开始从api获取数据，需要获取" + dataList.size() + "条数据");
		return dataList;
	}

	@SuppressWarnings({ "rawtypes" })
	public boolean readDeep() throws IOException {
		String sql = "SELECT COUNT(*) FROM "
				+ templet.getString("to_table_deep");
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		if (!"0".equals(((Map) ddr.readSingle()).get("COUNT(*)").toString())) {
			log.info("深度表中已经有数据，不需要再次从api获取！");
			return false;
		}
		dataList = getDeeps();

		// 直接写入数据库
		new WriteToDB().toDB(templet.getString("to_table_deep"), dataList);
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused", "static-access" })
	public boolean readRti() {
		String sql = "SELECT COUNT(*) FROM "
				+ templet.getString("to_table_rti");
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		if (!"0".equals(((Map) ddr.readSingle()).get("COUNT(*)").toString())) {
			log.info("动态表中已经有数据，不需要再次从api获取，只需要调用增量更新程序抓取！！");
			return false;
		}
		rtilList = new ArrayList();

		// 获取编码
		String charset = templet.getString("api_charset");
		HttpClientUtil hcu = new HttpClientUtil();
		String cityUrl = templet.getString("api_url_city");
		String Url = templet.getString("api_url_detail");
		String cityStr = hcu.httpGetReq(cityUrl, null, charset);
		String ziquanzong = "{\"citys\":" + cityStr;
		String last = ziquanzong + "}";
		Map cityMap = JsonUtil.parseMap(last);
		List<Map> cityList = (List) cityMap.get("citys");

		log.info("开始从api获取数据，需要获取" + cityList.size() + "个城市的数据");

		// 每个城市
		for (Map m : cityList) {
			String cityId = ObjectUtil.toString(m.get("i"));
			String cityName = ObjectUtil.toString(m.get("n"));
			for (int i = 1; i <= 100; i++) {
				String Str = hcu.httpGetReq(Url + cityId + "&p=" + i, null,
						charset);
				Map rti = JsonUtil.parseMap(Str);
				List<Map> list = (List) rti.get("l");
				if (list != null) {
					for (Map n : list) {
						// 替换掉n内部的引号，以免错误导致无法解析Json
						for (Object key : n.keySet()) {
							if (n.get(key) != null) {
								n.put(key,
										n.get(key).toString()
												.replace("\"", "”"));
							}
						}
						String jsonRti = JsonUtil.toJSONString(n);

						Map temp = new HashMap();
						temp.put("id", n.get("VenId"));
						temp.put("rtiid", n.get("i"));
						temp.put("performinfo", jsonRti);
						temp.put("state", n.get("s"));
						temp.put("update_flag", templet.UPDATE);

						rtilList.add(temp);
					}
				}
				if ((list == null || list.size() == 0)) {
					i = 101;
				}
			}
		}
		log.info("开始从api获取数据，需要获取" + rtilList.size() + "条动态数据");
		// 直接写入数据库
		sql = "insert into " + templet.getString("to_table_rti")
				+ " (id,rtiid,performinfo,state,update_flag)"
				+ "values(:id,:rtiid,:performinfo,:state,:update_flag)";
		DBDataWriter ddw = new DBDataWriter(sql);
		ddw.setDbenv(null);
		ddw.writeList(rtilList);
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public boolean readUpdateRti() {
		String rti_table = templet.getString("to_table_rti");
		List<Map> addList = new ArrayList();
		List<String> sqlList = new ArrayList();

		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		// 获取编码
		String charset = templet.getString("api_charset");
		HttpClientUtil hcu = new HttpClientUtil();
		String Url = templet.getString("api_url_update_rti");

		// 获取更新时间：最大精确到天
		String sql = "SELECT MAX(updatetime) FROM " + rti_table;
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		String rti_update_time = ((Map) ddr.readSingle())
				.get("MAX(updatetime)").toString();
		rti_update_time = rti_update_time.substring(0,
				rti_update_time.indexOf(" "));
		// rti_update_time = DateFormat.getDateInstance().format(new Date());
		// rti_update_time = "2013-07-12";

		// 读取增量信息
		String Str = hcu.httpGetReq(Url + rti_update_time, null, charset);
		log.info("当前访问串是：" + Url + rti_update_time);

		// 如果日期超过3天，则直接用当前时间替换
		if (Str != null && Str.contains("In excess of 3 days")) {
			rti_update_time = DateFormat.getDateInstance().format(new Date());
			Str = hcu.httpGetReq(Url + rti_update_time, null, charset);
		}
		Map rti = new HashMap();
		try {
			rti = JsonUtil.parseMap(Str);
		} catch (Exception e) {
			log.info("当前更新信息是：" + Str);
			return false;
		}
		if (rti.get("error") != null && !rti.get("error").equals("null")
				&& !rti.get("error").equals("")) {
			log.error("Error: " + rti.get("error"));
			return false;
		}
		List<Map> list = (List) rti.get("projectlist");
		if (list == null || list.size() == 0) {
			log.info("没有增量动态信息");
		} else {
			log.info("开始从api获取增量数据，需要获取" + list.size() + "个增量动态数据");
			for (Map m : list) {
				for (Object key : m.keySet()) {
					if (m.get(key) != null) {
						m.put(key, m.get(key).toString().replace("\"", "”"));
					}
				}
				Object rtiid = m.get("i");
				Object id = m.get("VenId");
				String sql0 = "select * from " + rti_table + " where rtiid = '"
						+ rtiid + "' and id = '" + id + "'";
				ddr = new DBDataReader(sql0);
				ddr.setDbenv(null);
				List<Map> data = ddr.readList();
				sql = null;
				if (data.size() == 1) {
					// 原表中有该条poi，进行更新,先删除原来数据，再插入
					String jsonRti = JsonUtil.toJSONString(m);
					sql = "delete from " + rti_table + " where rtiid = '"
							+ rtiid + "' and id = '" + id + "'";
					sqlList.add(sql);

					Map temp = new HashMap();
					temp.put("id", id);
					temp.put("rtiid", rtiid);
					temp.put("performinfo", jsonRti);
					temp.put("state", m.get("s"));
					temp.put("update_flag", templet.UPDATE);
					addList.add(temp);
				} else if (data.size() == 0) {
					// 新增信息
					String jsonRti = JsonUtil.toJSONString(m);

					Map temp = new HashMap();
					temp.put("id", id);
					temp.put("rtiid", rtiid);
					temp.put("performinfo", jsonRti);
					temp.put("state", m.get("s"));
					temp.put("update_flag", templet.UPDATE);

					addList.add(temp);
				} else {
					// 报错，不可能找到多条
					log.error("有两条同一rtiid的数据，错误！！！" + "rtiid is : " + rtiid);
					continue;
				}
			}
			log.info("开始更新通过api获取的动态信息数据，需要更新" + sqlList.size() + "条动态数据");
			dbexec.setSqlList(sqlList);
			dbexec.dbExec();

			log.info("开始写入通过api获取的新增动态信息数据，新增" + addList.size() + "条动态数据");
			sql = "insert into " + rti_table
					+ " (id,rtiid,performinfo,state,update_flag)"
					+ "values(:id,:rtiid,:performinfo,:state,:update_flag)";
			DBDataWriter ddw = new DBDataWriter(sql);
			ddw.setDbenv(null);
			ddw.writeList(addList);
		}
		return true;
	}

	// 获取全量动态信息
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getAllRtis() {
		List<Map> rtis = new ArrayList<Map>();
		// 获取编码
		String charset = templet.getString("api_charset");
		HttpClientUtil hcu = new HttpClientUtil();
		String Url = templet.getString("api_url_update_rti");

		int p = 1;
		String str = hcu.httpGetReq(Url + p, null, charset);
		while (!str.contains("{\"l\":[],\"t\":0}")
				&& !str.contains("{\"l\":null,\"t\":0}")) {
			str = hcu.httpGetReq(Url + p, null, charset);
			Map rti = JsonUtil.parseMap(str);
			List<Map> list = (List) rti.get("l");
			p++;
			if (list != null && !(list.size() == 0)) {
				for (Map m : list) {
					Map temp = new HashMap();
					temp.put("id", m.get("VenId"));
					temp.put("rtiid", m.get("i"));
					temp.put("performinfo", JSON.serialize(m));
					temp.put("state", m.get("s"));
					temp.put("update_flag", "1");
					rtis.add(temp);
				}
			}
		}
		log.info("共读取到的动态信息个数为：" + rtis.size());
		return rtis;
	}
}
