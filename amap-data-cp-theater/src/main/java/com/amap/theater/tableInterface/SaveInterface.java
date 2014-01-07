/**
 * @caoxuena
 * 2013-4-8
 *SaveInterface.java
 *根据传入的参数cp类型，将深度和动态信息有变化的都选出来，重新组装成save接口识别的json，并将更新标记置为0
 */
package com.amap.theater.tableInterface;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.http.HttpclientUtil;
import com.amap.base.utils.JsonUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class SaveInterface {
	private static final Logger log = LoggerFactory.getLogger(SaveInterface.class);
	// 设置日期格式,用于from中的update_time，每次都是获取系统当前时间
	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static String deepTable = "poi_deepinfo";
	private static String rtiTable = "poi_rti";

	// save接口访问串
	private static String urlString = "http://10.2.134.23:8080/amap_save/savepoi";

	/**
	 * 批量save 首先获取信息有变化的所有深度poiid;
	 * 根据深度poiid分别从deep表和rti表中获取base、deep和rti信息，同时组装from信息;
	 * 把获取到from、base、deep和rti信息封装成一个json串; 最后更新deep和rti表中的flag标记，置为0
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void combineSave(String cp) throws Exception {
		// 首先获取所有有变化的poiid（只获取对应poiid不为空的情况）
		List<Map> poiids = getUpdatePoiids(cp);

		// 根据有变化的id，获取对应的所有poiid
		for (Map poiidm : poiids) {
			Object poiid = poiidm.get("poiid");
			// 根据传入的poiid获取对应的所有id信息
			List<Map> ids = getIdsFromPoiid(poiid, cp);
			String combineJson = getCombineJson(poiid, ids, cp);
			Map m = new HashMap();
			m.put("json", combineJson);
			String result = HttpclientUtil.post(urlString, m, "UTF-8");
			if(!"{\"statuscode\":0,\"statusmsg\":\"success\"}".equalsIgnoreCase(result)){
				log.info("调用save接口时出错，出错的json串为：" + combineJson);
			}
			// 把deep和rti表中对应的flag标记设为0
			setUpdateFlag(poiid, ids, cp);
		}
	}

	/**
	 * 单条save 根据深度poiid分别从deep表和rti表中获取base、deep和rti信息，同时组装from信息;
	 * 把获取到from、base、deep和rti信息封装成一个json串; 最后更新deep和rti表中的flag标记，置为0
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void combineSave(Map data, String poiid) throws Exception {
		String combineJson = getCombineJson(poiid, data);
		Map m = new HashMap();
		m.put("json", combineJson);
		String result = HttpclientUtil.post(urlString, m, "UTF-8");
		if (!"{\"statuscode\":0,\"statusmsg\":\"success\"}"
				.equalsIgnoreCase(result)) {
			System.out.println(result);
		}
	}

	/**
	 * 根据传入的poiid，获取from、base、deep和rti合并后的整体json串;
	 * 如果该poiid对应多个id（包括深度和动态），则深度取最新的那个，动态合并
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String getCombineJson(Object poiid, List<Map> ids, String cp) {
		LinkedHashMap combineMap = new LinkedHashMap();
		combineMap.put("poiid", poiid);

		// 获取base和deep信息
		combineMap = combineFromBaseDeep(combineMap, ids, cp);
		// 获取动态rti信息
		combineMap = combineRtis(combineMap, ids, cp);

		return JSON.serialize(combineMap);
	}
	
	/**
	 * 根据传入的poiid，获取from、base、deep和rti合并后的整体json串;
	 * 针对单条调用save接口的情况
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String getCombineJson(String poiid, Map data) {
		LinkedHashMap combineMap = new LinkedHashMap();
		combineMap.put("poiid", poiid);

		//get from info
		combineMap.put("from", getFrom(data.get("id"), data.get("url"), data.get("update_flag"), data.get("cp").toString()));
		//get base info
		LinkedHashMap base = (LinkedHashMap) JSON.parse(data.get("base").toString());
		base.put("poiid", poiid);
		combineMap.put("base", base);
		// 获取deep信息
		combineMap.put("deep", (LinkedHashMap) JSON.parse(data.get("deep").toString()));
		// 获取动态rti信息
		combineMap.put("rti", getRtis(data.get("cp").toString(), data.get("id").toString()));

		return JSON.serialize(combineMap);
	}

	/**
	 * 根据传入的cp和cpid获取对应的动态信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getRtis(String cp, String id){
		List<Map> rtis = new ArrayList<Map>();
		String sql = "SELECT * FROM " + rtiTable + " WHERE cp = '" + cp
				+ "' AND id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		// 先
		List<Map> rtiis = ddr.readAll();
		if (rtiis == null || rtiis.size() == 0) {
			return null;
		}
		Map rtiinfo = (Map) ddr.readSingle();
		if (assertRtiExist(rtiinfo, cp)) {
			List<LinkedHashMap> idRtis = (List<LinkedHashMap>) JSON
					.parse(rtiinfo.get("rti").toString());
			for (LinkedHashMap idRti : idRtis) {
				rtis.add(idRti);
			}
		}
		return rtis.size() == 0 ? null : rtis;
	}
	/**
	 * 如果该poiid下只有一个id，则直接取其base和deep信息； 多个的话，获取更新时间最新的base和deep信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap combineFromBaseDeep(LinkedHashMap combineMap,
			List<Map> ids, String cp) {
		// 用第一条的信息初始化
		String sql = "SELECT * FROM " + deepTable + " WHERE cp = '" + cp
				+ "' AND id = '" + ids.get(0).get("id") + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		Map deepinfo = (Map) ddr.readSingle();
		LinkedHashMap base = (LinkedHashMap) (deepinfo.get("base") == null ? null
				: JsonUtil.parseMap(deepinfo.get("base").toString()));
		LinkedHashMap deep = (LinkedHashMap) (deepinfo.get("deep") == null ? null
				: JsonUtil.parseMap(deepinfo.get("deep").toString()));
		Object url = deepinfo.get("url");
		Object cpid = deepinfo.get("id");
		Object update_flag = deepinfo.get("update_flag");

		// 以第一条的更新时间为基准，选择时间最新的
		String update_time = deepinfo.get("updatetime").toString();
		for (int i = 1; i < ids.size(); i++) {
			Object id = ids.get(i).get("id");
			sql = "SELECT * FROM " + deepTable + " WHERE cp = '" + cp
					+ "' AND id = '" + id + "'";
			ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			deepinfo = (Map) ddr.readSingle();
			// 如果是已删除数据，continue
			if ("2".equals(deepinfo.get("update_flag"))) {
				continue;
			}
			String time = deepinfo.get("updatetime").toString();
			if (time.compareToIgnoreCase(update_time) > 0) {
				update_time = time;
				base = (LinkedHashMap) (deepinfo.get("base") == null ? null
						: JsonUtil.parseMap(deepinfo.get("base").toString()));
				deep = (LinkedHashMap) (deepinfo.get("deep") == null ? null
						: JsonUtil.parseMap(deepinfo.get("deep").toString()));
				url = deepinfo.get("url");
				cpid = deepinfo.get("id");
				update_flag = deepinfo.get("update_flag");
			}
		}
		combineMap.put("from", getFrom(cpid, url, update_flag, cp));
		if(base != null){
			combineMap.put("base", base);
		}
		if(deep != null){
			combineMap.put("deep", deep);
		}
		return combineMap;
	}

	/**
	 * 考虑一个poiid对应多条rti的情况，此时需要对所有的rti进行组装； 把多组rti合并成一个json
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LinkedHashMap combineRtis(LinkedHashMap combineMap,
			List<Map> ids, String cp) {
		List<Map> rtis = new ArrayList<Map>();
		for (Map idm : ids) {
			Object id = idm.get("id");
			String sql = "SELECT * FROM " + rtiTable + " WHERE cp = '" + cp
					+ "' AND id = '" + id + "'";
			DBDataReader ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			// 先
			List<Map> rtiis = ddr.readAll();
			if (rtiis == null || rtiis.size() == 0) {
				continue;
			}
			Map rtiinfo = (Map) ddr.readSingle();
			if (assertRtiExist(rtiinfo, cp)) {
				List<LinkedHashMap> idRtis = (List<LinkedHashMap>) JSON
						.parse(rtiinfo.get("rti").toString());
				for (LinkedHashMap idRti : idRtis) {
					rtis.add(idRti);
				}
			}
		}

		combineMap.put("rti", rtis.size() == 0 ? null : rtis);
		return combineMap;
	}

	/**
	 * 根据传入的poiid获取深度表中所有的id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map> getIdsFromPoiid(Object poiid, String cp) {
		String sql = "SELECT id FROM " + deepTable + " WHERE poiid = '" + poiid
				+ "' and cp = '" + cp + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readList();
	}

	/**
	 * 根据传入的参数类型，选出该类型下有更新的所有poiid（深度或动态有一个更新都要进行更新）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map> getUpdatePoiids(String cp) {
		String sql = "SELECT DISTINCT poiid FROM " + deepTable
				+ " WHERE id IN (" + "SELECT a.id FROM " + deepTable
				+ " a LEFT JOIN " + rtiTable
				+ " b ON a.id = b.id WHERE ((a.cp = '" + cp
				+ "' and a.update_flag!=0) OR (b.cp = '" + cp
				+ "' and b.update_flag!=0) ) )"
				+ "AND poiid IS NOT NULL AND poiid != 'null'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readList();
	}

	/**
	 * 根据update_flag，获取opt_type标记：update_flag为2时代表数据删除；否则为修改标记
	 */
	private static String GetOptType(Object update_flag) {
		if ("2".equals(update_flag)) {
			return "d";
		}
		return "u";
	}

	/**
	 * 根据传入的深度信息，封装from字段信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getFrom(Object id, Object url, Object update_flag,
			String cp) {
		Map from = new LinkedHashMap();
		from.put("opt_type", GetOptType(update_flag));

		// 获取update_time
		from.put("update_time", getUpdateTime());
		// TODO:来源暂时用以前的命名,cp值为“theater_damai_api”
		from.put("src_type", cp);
		// TODO:src_version如何取值
		from.put("src_version", "1");
		from.put("src_id", id);
		from.put("src_url", url);
		return from;
	}

	/**
	 * 更新时间以当前的系统时间为准
	 */
	private static Object getUpdateTime() {
		return df.format(new Date());
	}

	/**
	 * 经过save接口处理之后，把对应信息的标记位置为0;
	 */
	@SuppressWarnings("rawtypes")
	private static void setUpdateFlag(Object poiid, List<Map> ids, String cp) {
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		String sql = "update " + deepTable
				+ " set update_flag = 0 where cp = '" + cp + "' and poiid = '"
				+ poiid + "'";
		dbexec.setSql(sql);
		dbexec.dbExec();

		// 如果动态信息存在，则对应更新动态表
		for (Map idm : ids) {
			Object id = idm.get("id");
			if (assertRtiExist(id, cp)) {
				sql = "update " + rtiTable
						+ " set update_flag = 0 where cp = '" + cp
						+ "' and id = '" + id + "'";
				dbexec.setSql(sql);
				dbexec.dbExec();
			}
		}
	}

	/**
	 * 根绝传入的id，判断当前cp下是否有该条动态信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean assertRtiExist(Object id, String cp) {
		String sql = "SELECT * FROM " + rtiTable + " WHERE cp = '" + cp
				+ "' AND id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rtiinfo = ddr.readAll();
		if (rtiinfo == null || rtiinfo.size() < 1) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	private static boolean assertRtiExist(Map m, String cp) {
		if (m == null || m.size() < 1) {
			return false;
		}
		return true;
	}
}
