/**
 * 2013-5-9
 */
package com.amap.data.save;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.http.HttpclientUtil;
import com.amap.base.utils.JsonUtil;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.match.NewMatch;
import com.amap.data.save.picDeal.PicDeal;
import com.amap.data.save.task.ApiDeepTask;
import com.amap.data.save.task.ApiRtiTask;
import com.amap.data.save.transfer.ApiRtitransfer;
import com.amap.data.save.transfer.Apitransfer;
import com.mongodb.util.JSON;

/**
 * 定义定期扫描深度和动态中间表的参数及相关函数
 */
public class Save {
	private final Logger log = LoggerFactory.getLogger(Save.class);
	// 一次最多处理的poiid个数：分批次处理，方便从sql中读取数据
	private static final int poiidNum = 10000;

	// 用于记录第一个和最后一个处理的poiid
	private static Object firstPoiid = null;
	private static Object lastPoiid = null;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	// 用于标记数据是入正式环境还是测试环境，true代表入测试环境，false代表入正式环境
	private static boolean test_flag = true;
	// 正式环境：
	/*
	 * 外网地址： http://211.151.71.27:8010/amap_save/savepoi 内网地址：
	 * http://192.168.3.212:80/amap_save/savepoi
	 */
//	 private static String urlString =
//	 "http://10.2.150.98:8080/amap_save/savepoi";
	private static String urlString = "http://192.168.3.215:8081/amap_save/savepoi";
	private static String saveAllUrl = "http://192.168.3.215:8081/amap_save/saveall";
	protected static String update_flag = "update_flag";
	// 判断是否是新增poiid的url
	private static String assertnewpoiidUrl = "http://192.168.3.215:8081/amap_save/poiexists";
	// private static String saveAllUrl =
	// "http://10.2.166.174:8080/amap_save/saveall";
	// 用于标记是否是新增poiid
	private static Boolean isnew_poiid_flag = null;

	protected final String deep_table = "poi_deep";
	protected final String rti_table = "poi_rti";
	protected final String error_table = "poi_error";
	protected final String newpoi_table = "poi_newpoi";
	protected ApiDeepTask deeptask;
	protected ApiRtiTask rtitask;
	protected TempletConfig templet;

	@SuppressWarnings("rawtypes")
	private List<Map> poiids = new ArrayList<Map>();
	private List<String> sqlList = new ArrayList<String>();

	// 用于记录可以入测试或线上的所有cp
	private Set<String> cpSets = new HashSet<String>();

	@SuppressWarnings("rawtypes")
	private List<Map> validIds = new ArrayList<Map>();
	@SuppressWarnings("rawtypes")
	private Map deepMap = new HashMap();
	
	//新匹配的poiid
	@SuppressWarnings("rawtypes")
	private Map newpoiidInfos = new HashMap();
	private boolean hasNewPoiidInfo = false;

	private int count;
	private int rightnum;
	private int countNotAssNum;

	private void initUrlFlag() {
		if (test_flag) {
			// 测试环境，数据在106的cms_v3上
			// http://10.2.166.174:8080/amap_save/savepoi
			urlString = "http://10.2.134.23:8080/amap_save/savepoi";
			// urlString = "http://10.2.166.174:8080/amap_save/savepoi";
			assertnewpoiidUrl = "http://10.2.134.23:8080/amap_save/poiexists";
			saveAllUrl = "http://10.2.134.23:8080/amap_save/saveall";
			update_flag = "test_update_flag";
			log.info("目前入的是测试库");
		} else {
			log.info("目前入的是正式环境！！！");
		}
	}

	private void inintCpSets() {
		if (test_flag) {
			inintCpSetsTest();
		} else {
			inintCpSetsOnline();
		}
	}

	/**
	 * 测试环境上新增上线的cp类型
	 */
	private void inintCpSetsTest() {
		cpSets.add("dining_dianping_api");
		cpSets.add("ali_qqfood");
		cpSets.add("ali_qunar");
		// cpSets.add("ali_soufun");
		cpSets.add("ali_kaifanla");
		cpSets.add("ali_sinahouse");
		cpSets.add("ali_fantong");
		cpSets.add("qingbao_base");

		// 点评其他行业
		// cpSets.add("hospital_dianping_api");
		cpSets.add("theater_dianping_api");
		cpSets.add("hotel_dianping_api");
		// cpSets.add("education_dianping_api");
		cpSets.add("shopping_dianping_api");
		cpSets.add("enjoy_dianping_api");
		cpSets.add("dianping_api");
		// cpSets.add("building_dianping_api");
		// cpSets.add("residential_dianping_api");
		cpSets.add("car_dianping_api");

		// 团购新增
		cpSets.add("groupbuy_tuan800_api");
		cpSets.add("groupbuy_meituan_api");
		cpSets.add("groupbuy_lashou_api");
		// cpSets.add("groupbuy_like_api");

		// 道路交叉点
		cpSets.add("road_cross");

		// 携程和艺龙 新增上线
		cpSets.add("hotel_ctrip_wireless_api");
		cpSets.add("hotel_elong_api");

		// 门址新增入测试库
		cpSets.add("site_collect");

		// 精品新增入测试库
		cpSets.add("jingpin_special_shop");

		// 事件poi
		cpSets.add("event_poi");
		
		//dianping_refresh
		cpSets.add("dianping_refresh");
		
		cpSets.add("hd");
	}

	/**
	 * 正式环境上新增上线的cp类型
	 */
	private void inintCpSetsOnline() {
		cpSets.add("dining_dianping_api");
		cpSets.add("ali_qqfood");
		cpSets.add("ali_qunar");
		// cpSets.add("ali_soufun");
		cpSets.add("ali_kaifanla");
		cpSets.add("ali_sinahouse");
		cpSets.add("ali_fantong");
		cpSets.add("qingbao_base");

		// 团购新增
		cpSets.add("groupbuy_tuan800_api");
		cpSets.add("groupbuy_meituan_api");
		cpSets.add("groupbuy_lashou_api");

		// 点评非餐饮行业新增
		cpSets.add("hotel_dianping_api");
		cpSets.add("shopping_dianping_api");
		cpSets.add("enjoy_dianping_api");
		cpSets.add("dianping_api");
		cpSets.add("car_dianping_api");
		cpSets.add("theater_dianping_api");

		// 道路交叉点
		cpSets.add("road_cross");

		// 携程和艺龙 新增上线
		cpSets.add("hotel_ctrip_wireless_api");
		cpSets.add("hotel_elong_api");

		// 门址新增上线
		cpSets.add("site_collect");

		// 精品新增上线
		cpSets.add("jingpin_special_shop");

		// 事件poi
		cpSets.add("event_poi");
		
		//dianping_refresh
		cpSets.add("dianping_refresh");
	}

	private void initNum() {
		count = 0;
		rightnum = 0;
		countNotAssNum = 0;
	}

	public void init(String cp) {
		// 初始化深度处理程序
		TempletConfig deeptemplet = new TempletConfig(cp + "_deep_1");
		Apitransfer tranfer = new Apitransfer();
		deeptask = new ApiDeepTask(deeptemplet, tranfer);
		deeptask.init();

		// 处理动态信息
		TempletConfig rtitemplet = new TempletConfig(cp + "_rti_1");
		ApiRtitransfer rtiTranfer = new ApiRtitransfer();
		rtitask = new ApiRtiTask(rtitemplet, rtiTranfer);
		rtitask.init();
	}

	/**
	 * 把标记位为-1的置为1，再次调用接口看库中是否有对应的poiid
	 */
	public void initFlag(String cp) {
		String sql = "UPDATE " + deep_table + " SET " + update_flag
				+ " = 1 WHERE cp = '" + cp + "' and " + update_flag + " = -1";
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		dbexec.setSql(sql);
		dbexec.dbExec();
	}

	/**
	 * 根据传入的cp，判断当前cp对应的深度或动态是否有数据更新，有的话，返回true；否则返回false
	 */
	public boolean assertData(String cp) {
		if (assertDeepData(cp) || assertRtiData(cp)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取更新的深度信息：poiid必须有值
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private boolean assertDeepData(String cp) {
		String sql = "SELECT * FROM " + deep_table + " WHERE cp = '" + cp
				+ "' AND poiid IS NOT NULL AND " + update_flag + " = "
				+ templet.UPDATE + " limit :from, :size";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> deepDatas = ddr.readList();
		if (deepDatas != null && deepDatas.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取更新的动态信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private boolean assertRtiData(String cp) {
		String sql = "SELECT * FROM " + rti_table + " WHERE cp = '" + cp
				+ "' AND " + update_flag + " = " + templet.UPDATE
				+ " limit :from,:size";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rtiDatas = ddr.readList();
		if (rtiDatas != null && rtiDatas.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 根据传入的cp，先判断是否有更新数据，有的话，进行处理，并调用save接口； 没有的话，返回；继续监测
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public boolean dealSave(String cp, boolean hasDeep) throws Exception {
		// 防止调用save接口时可能出现多线程并发无法成功入库的情况，最多执行3次，一般情况下一万条可能一两条发生无法成功上线
		for (int i = 0; i < 1; i++) {
			initUrlFlag();
			init(cp);
			initNum();
			inintCpSets();

			// 有些数据不能走新增，所以flag被置为-1，下次入库时需要恢复flag标记
			initFlag(cp);

//			 if(true){
//			 log.info("获取测试用poiid");
//			 try {
//			 poiids = SaveHelper.getPoiidFromCsv();
//			 } catch (IOException e) {
//			 }
//			 }

			 Map temp = new HashMap();
			 temp.put("poiid", "B037C0PM8U");
			 poiids.add(temp);
			int lastnum = 0;
//			poiids = getUpdatePoiids(cp, poiidNum);
			if(poiids == null || poiids.size() == 0){
				log.info("本次没有数据更新！");
				return true;
			}
			
			// 加载新的匹配关系,并初始化标记位
			initNewpoiidInfos(cp);
			do {
				lastnum = poiids.size();
				for (Map poiidm : poiids) {
					String poiid = poiidm.get("poiid").toString();
					// 加入新增入库流程：判断是否是新增，如果不是，则需要走匹配流程
					assertIsNewPoiid(poiid);
					if (isnew_poiid_flag == null) {
						log.info("当前没有判断出该poiid是否是新增数据，poiid是：" + poiid);
						continue;
					}
					if (isnew_poiid_flag && !assertCpAdd(cp)) {
						// 当前poiid是新增，但是新增不入库，把标记位置为-1
						setAddFlag(poiid, cp);
						continue;
					}

					count++;
					boolean flag = getCombineAndDeal(poiid, cp, hasDeep);
					// 处理标记位
					if (rightnum != 0 && rightnum % 500 == 0) {
						setUpdateFlag();
					}
					if (!flag) {
						continue;
					}
				}
				if (sqlList.size() > 0) {
					setUpdateFlag();
				}
				if (lastnum < poiidNum) {
					poiids = null;
				} else {
					poiids = getUpdatePoiids(cp, poiidNum);
				}
			} while ((poiids != null && poiids.size() > 0)
					&& (!(poiids.size() == lastnum && lastnum < poiidNum)));
			log.info("本次共有更新的poiid个数为：" + count + "个，成功入库：" + rightnum
					+ "个， 没有组装： " + countNotAssNum + "个");
			log.info("本次第一个成功入库的poiid是：" + firstPoiid + "；最后一个成功入库的poiid是： "
					+ lastPoiid);
			log.info("本次save成功执行完毕！");
			if (rightnum == count) {
				return true;
			}
		}
		return true;
	}
	
	/**
	 * 新增但是 没法入库的，需要处理标记位为-1
	 */
	@SuppressWarnings("rawtypes")
	private void setAddFlag(Object poiid, String cp){
		List<Map> ids = getIdsFromPoiid(poiid, cp);
		setUpdateFlagForAdd(poiid, ids, cp);
	}
	
	//已经处理成功的，把标记位置为0
	private void setUpdateFlag(){
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();
		sqlList = new ArrayList<String>();
	}

	/**
	 * 判断当前cp是否在测试新增可以上线的cp中;在的话返回true，否则返回false
	 */
	private boolean assertCpAdd(String cp) {
		if (cpSets != null && cpSets.contains(cp)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前poiid是否是新增poiid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertIsNewPoiid(String poiid) {
		Map m = new HashMap();
		m.put("poiid", poiid);
		String result = "";
		try {
			result = HttpclientUtil.post(assertnewpoiidUrl, m, "UTF-8");
		} catch (Exception e) {
			log.info("判断是否是新增poiid异常，异常原因是：" + e);
		}
		if (result.contains("success")) {
			isnew_poiid_flag = false;
		} else if (result.contains("WithoutThisPoiId")) {
			isnew_poiid_flag = true;
		} else {
			isnew_poiid_flag = null;
		}
	}

	/**
	 * 用于处理错误匹配的数据
	 */
	public boolean dealSave(String cp, boolean hasDeep, String poiid,
			String id, boolean testflag) {
		test_flag = testflag;
		initUrlFlag();
		init(cp);
		boolean flag = getCombineAndDeal(poiid, cp, hasDeep, id);
		return flag;
	}

	// 根据传入的poiid和cp进行处理,针对错误匹配进行处理
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean getCombineAndDeal(String poiid, String cp,
			boolean hasDeep, String id) {
		// 根据传入的poiid获取对应的所有id信息
		List<Map> ids = getIdsFromPoiid(poiid, cp);

		String combineJson = null;
		if (ids == null || ids.size() == 0) {
			// 只拼装简单的下线信息
			combineJson = assemble(poiid, cp);
			String result = "";
			// 调用save接口，获取result
			try {
				Map m = new HashMap();
				m.put("json", combineJson);
				result = HttpclientUtil.post(urlString, m, "UTF-8");
			} catch (Exception e) {
				log.info("调用save接口出错，错误原因是：" + e);
				return false;
			}

			dealResult(result, poiid, id, cp, combineJson);
		} else {
			String result = "";
			// 获取combine
			try {
				combineJson = getCombineJson(poiid, ids, cp, hasDeep);
				// System.out.println(combineJson);
			} catch (Exception e) {
				log.info("获取combineJson时出错，错误原因是：" + e + "；错误的poiid是：" + poiid);
				return false;
			}
			// 调用save接口，获取result
			try {
				Map m = new HashMap();
				m.put("json", combineJson);
				result = HttpclientUtil.post(urlString, m, "UTF-8");
			} catch (Exception e) {
				log.info("调用save接口出错，错误原因是：" + e);
				return false;
			}
			// 根据返回result，进行特定处理，更新表中标记位
			if (id != null && !id.equals("")) {
				dealResult(result, poiid, id, cp, combineJson);
			}
			dealResult(result, poiid, ids, cp, combineJson);
		}
		return true;
	}

	/**
	 * 拼装下线信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String assemble(String poiid, String cp) {
		Map m = new LinkedHashMap();
		m.put("poiid", poiid);
		Map from = new LinkedHashMap();
		from.put("src_type", cp);
		from.put("update_time", sdf.format(new Date()));
		from.put("opt_type", "d");
		m.put("from", from);
		return JsonUtil.toJSONString(m);
	}

	// 根据传入的poiid和cp进行处理
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean getCombineAndDeal(String poiid, String cp, boolean hasDeep)
			throws Exception {
		// 根据传入的poiid获取对应的所有id信息
		List<Map> ids = getIdsFromPoiid(poiid, cp);
		if (ids == null || ids.size() == 0) {
			log.info("当前poiid没有对应的id信息，poiid是：" + poiid);
			return false;
		}
		// 获取有效非下线的ids信息
		LinkedHashMap temp = new LinkedHashMap();
		temp.put("poiid", poiid);
		// 找出非下线数据id
		validIds = getValidIds(temp, ids, cp);
		
		//如果是新增，并且没有有效的poiid信息
		if ((validIds == null || validIds.size() == 0) && isnew_poiid_flag){
			setAddFlag(poiid, cp);
			return true;
		}
		//初始化
		String combineJson = null;
		String result = "";
		// 获取combine
		try {
			combineJson = getCombineJson(poiid, ids, cp, hasDeep);
//			 System.out.println(combineJson);
		} catch (Exception e) {
			log.info("获取combineJson时出错，错误原因是：" + e + "；错误的poiid是：" + poiid);
			return false;
		}
		// 如果是新增的，需要调用匹配接口，接口返回有两种情况：null或者和poiid不同
		if (isnew_poiid_flag != null && isnew_poiid_flag) {
			// 调用匹配接口
			if (validIds != null && validIds.size() > 0) {
				// 非空字段检查，如果对应字段有空的，则不上线
				if (!NewPoiidChecked.checkedNotNull(combineJson)) {
					return false;
				}
				// 有些新增不上线，比如百度数据的地名poi等
				if (!assertAddRegular(combineJson)) {
					return false;
				}
				String newPoiid = null;
				try {
					// 旧匹配
					if (test_flag) {
						newPoiid = Match.getMatchPoiid(combineJson, test_flag);
					} else {
						// 新匹配
						newPoiid = NewMatch.getMatchPoiid(combineJson, false);
					}
				} catch (UnsupportedEncodingException e) {
					log.info("匹配出错");
					return false;
				}
				if (newPoiid != null && newPoiid.equalsIgnoreCase("false")) {
					return false;
				}

				// 2013.11.08:新增的poiid，处理from中的opt_type为a
				combineJson = updateOptType(combineJson);

				// 调用save接口
				try {
					Map m = new HashMap();
					m.put("json", combineJson);
					m.put("mergedid", poiid);
					m.put("usingid", newPoiid == null ? poiid : newPoiid);
					result = HttpclientUtil.post(saveAllUrl, m, "UTF-8");
				} catch (Exception e) {
					log.info("调用saveall接口出错，错误原因是：" + e);
					return false;
				}
			} else {
				return true;
			}
		} else {
			// 调用save接口，获取result
			try {
				Map m = new HashMap();
				m.put("json", combineJson);
				result = HttpclientUtil.post(urlString, m, "UTF-8");
			} catch (Exception e) {
				log.info("调用save接口出错，错误原因是：" + e);
				return false;
			}
		}
		// 根据返回result，进行特定处理，更新表中标记位
		dealResult(result, poiid, ids, cp, combineJson);
		return true;
	}

	/**
	 * 新增数据 from中的opt_type改为a
	 */
	private String updateOptType(String combineJson) {
		return combineJson.replace("\"from\" : { \"opt_type\" : \"u\"",
				"\"from\" : { \"opt_type\" : \"a\"");
	}

	/**
	 * 判断新增数据是否能上线，不能的话，返回false；
	 */
	public boolean assertAddRegular(String combineJson) {
		return true;
	}

	@SuppressWarnings("rawtypes")
	protected boolean dealResult(String result, Object poiid, List<Map> ids,
			String cp, String combineJson) {
		if ("{ \"statuscode\" : 0 , \"statusmsg\" : \"success\"}"
				.equalsIgnoreCase(result) || result.contains("success")) {
			// 第一个成功处理的poiid
			if (firstPoiid == null) {
				firstPoiid = poiid;
			}
			// 最后一个成功处理的poiid
			lastPoiid = poiid;
			// 成功入库，更新标记信息
			// 把deep和rti表中对应的flag标记设为0
			setUpdateFlag(poiid, ids, cp);
			rightnum++;
			if (rightnum % 100 == 0) {
				log.info("已经成功处理poiid个数：" + rightnum);
			}
		} else if (result.contains("NotAssembled")) {
			// log.info("当前信息没有组装，没有组装的json串为：" + combineJson);
			countNotAssNum++;
			if (countNotAssNum % 100 == 0) {
				log.info("没有组装的poiid个数：" + countNotAssNum);
			}
			// 把deep和rti表中对应的flag标记设为0
			setUpdateFlag(poiid, ids, cp);
		} else {
			log.info("调用save接口时出错，出错的原因为：" + result);
			log.info("出错的json串为：" + combineJson);
			return false;
		}
		return true;
	}

	/**
	 * 纠错处理代码
	 */
	protected boolean dealResult(String result, Object poiid, String id,
			String cp, String combineJson) {
		if ("{ \"statuscode\" : 0 , \"statusmsg\" : \"success\"}"
				.equalsIgnoreCase(result) || result.contains("success")) {
			// 成功入库，更新标记信息
			// 把deep和rti表中对应的flag标记设为0
			setUpdateFlag(poiid, id, cp);
		} else if (result.contains("NotAssembled")) {
			// log.info("当前信息没有组装，没有组装的json串为：" + combineJson);
			setUpdateFlag(poiid, id, cp);
		} else {
			log.info("调用save接口时出错，出错的原因为：" + result);
			log.info("出错的json串为：" + combineJson);
			return false;
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	protected String getCombineJson(Object poiid, List<Map> ids, String cp,
			boolean hasDeep) {
		LinkedHashMap combineMap = new LinkedHashMap();
		combineMap.put("poiid", poiid);

		// 当前信息不是下线信息，组装全部需要字段
		if (validIds != null && validIds.size() > 0) {
			if ("cinema_kokozu_api".equalsIgnoreCase(cp) && validIds.size() > 1) {
				// 抠电影多对一 下线
				combineMap.put("from", SaveHelper.getFrom(ids.get(0).get("id"),
						null, templet.UPDATE, cp));
			} else {
				// 获取base字段，直接从deep表中取数据即可
				combineMap = combineBase(combineMap, validIds, cp);

				if (hasDeep) {
					Map result = combineDeeps(combineMap, validIds, cp);
					// 获取from和deep信息
					combineMap = combineFromDeep(combineMap, result, validIds,
							cp);
				} else {
					// 没有深度，需要单独获取from信息
					combineMap.put("from", SaveHelper.getFrom(validIds.get(0)
							.get("id"), null, templet.UPDATE, cp));
				}
				// 获取动态rti信息
				combineMap = combineRtis(combineMap, validIds, cp);

				// 一些特殊cp的特殊处理：比如一块去需要增加spec字段
				combineMap = combineSpec(combineMap, cp);

				// 获取idDictionaries信息
				combineMap = combineidDictionaries(combineMap, cp);
			}
		} else {
			// 当前信息是下线信息，只组装poiid和from字段
			combineMap.put("from", SaveHelper.getFrom(ids.get(0).get("id"),
					null, templet.UPDATE, cp));
		}

		// 增加图片处理
		combineMap = getPics(combineMap);
		
		//当前cp下有需要替换的poiid关系：新匹配关系表中有对应的新的poiid，需要进行poiid替换，把旧的替换为新的
		if(hasNewPoiidInfo){
			combineMap = getNewPoiidCombine(combineMap, cp);
		}
		
		return JSON.serialize(combineMap);
	}
	
	/**
	 * 用新的poiid替换旧的poiid，替换外层和base内部的poiid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getNewPoiidCombine(LinkedHashMap combineMap, String cp) {
		//获取id
		Map from = (Map) combineMap.get("from");
		Object id = from.get("src_id");
		Object newpoiid = newpoiidInfos.get(cp + "&" + id);
		LinkedHashMap base = (LinkedHashMap) combineMap.get("base");
		base.put("poiid", newpoiid);
		combineMap.put("base", base);
		combineMap.put("poiid", newpoiid);
		return combineMap;
	}

	/**
	 * 增加图片处理：从图片服务器获取图片
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getPics(LinkedHashMap combineMap) {
		for (Object key : combineMap.keySet()) {
			combineMap
					.put(key, PicDeal.getMapAfterPicDeal(combineMap.get(key)));
		}
		return combineMap;
	}

	/**
	 * 组装base字段，直接从深度表中获取，不需要自己再拼装
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LinkedHashMap combineBase(LinkedHashMap combineMap,
			List<Map> ids, String cp) {
		Map deep = getNewestDeep(combineMap, ids, cp);
		Object deepinfo = deep.get("deep");
		deepMap = (Map) JSON.parse(SaveHelper.getTranserJson(deepinfo));
		deepMap = SaveHelper.transferToSmall(deepMap);
		deepMap.put("cp", cp);
		deepMap.put("poiid", deep.get("poiid"));
		deepMap.put("id", deep.get("id"));

		if (deepMap.get("base") == null || deepMap.get("base").equals("")) {
			return combineMap;
		}
		LinkedHashMap baselink = (LinkedHashMap) deepMap.get("base");
		// 判断base字段是否完整：完整的符合3.0规格的话就推base字段；否则不推base
		if (baselink != null && SaveHelper.assertBase(baselink)) {
			combineMap.put("base", baselink);
		}
		return combineMap;
	}

	/**
	 * 深度信息融合：如果包含多个深度信息的话，则进行融合，选择最新的那个
	 */
	@SuppressWarnings({ "rawtypes" })
	private Map combineDeeps(LinkedHashMap combineMap, List<Map> ids, String cp) {
		// 大麦网B000A82ZE2强制滤掉小图
		if ("theater_damai_api".equalsIgnoreCase(cp)
				&& "B000A82ZE2".equalsIgnoreCase(deepMap.get("poiid")
						.toString())) {
			deepMap.remove("pic");
		}
		return deepMap;
	}

	/**
	 * 如果该poiid下只有一个id，则直接取其base和deep信息； 多个的话，获取更新时间最新的base和deep信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap combineFromDeep(LinkedHashMap combineMap, Map result,
			List<Map> ids, String cp) {
		String json = JSON.serialize(result);
		result = deeptask.toApiTranser(json);
		// 需要自己组装from信息
		if (result.get("opt_type") == null) {
			combineMap.put("from", SaveHelper.getFrom(result, cp));
		} else {
			// 来源信息中已经有from字段，直接拿来用即可
			combineMap.put("from", SaveHelper.combineFrom(result, cp));
		}
		LinkedHashMap deeplink = (LinkedHashMap) (result.get("deep") == null ? null
				: JsonUtil.parseMap(result.get("deep").toString()));
		combineMap.put("deep", deeplink);
		return combineMap;
	}

	/**
	 * 找出当前poiid下，所有非下线的id，并返回
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<Map> getValidIds(LinkedHashMap combineMap, List<Map> ids,
			String cp) {
		List<Map> validIds = new ArrayList<Map>();
		// 获取深度信息
		List<Map> deeps = new ArrayList<Map>();

		deeps = getAllDeeps(combineMap, ids, cp);

		// 判断并取出有效的深度id
		for (Map deep : deeps) {
			Object id = deep.get("id");
			Map idMap = new HashMap();
			idMap.put("id", id);
			Map deepMap = new HashMap();
			try {
				String deepStr = deep.get("deep").toString();
				deepMap = (Map) JSON.parse(deepStr);
				deepMap = SaveHelper.transferToSmall(deepMap);
				if (deepMap == null) {
					validIds.add(idMap);
					continue;
				}
			} catch (Exception e) {
				continue;
			}
			Object statusObj = deepMap.get("status") + "";
			if (statusObj != null
					&& statusObj.toString().equalsIgnoreCase("-1")) {
				continue;
			} else {
				validIds.add(idMap);
			}
		}
		return validIds;
	}

	/**
	 * 多条深度信息的话，获取更新时间最新的深度信息
	 */
	@SuppressWarnings({ "rawtypes" })
	protected Map getNewestDeep(LinkedHashMap combineMap, List<Map> ids,
			String cp) {
		List<Map> deeps = getAllDeeps(combineMap, ids, cp);
		Map newestDeep = deeps.get(0);
		if (ids.size() > 1) {
			newestDeep = getValidDeep(deeps);
		}
		return SaveHelper.transferToSmall(newestDeep);
	}

	/**
	 * 获取当前cp和id下的所有deep信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<Map> getAllDeeps(LinkedHashMap combineMap, List<Map> ids,
			String cp) {
		String idSql = "id = '";
		for (Map id : ids) {
			idSql = idSql
					+ (idSql == "id = '" ? id.get("id") : " or id = '"
							+ id.get("id"));
			idSql += "'";
		}
		Object poiid = combineMap.get("poiid");
		String sql = "select * from " + deep_table + " where poiid = '" + poiid
				+ "' and cp = '" + cp + "' and (" + idSql + ")";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> deeps = ddr.readAll();
		return deeps;
	}

	// 多个深度信息的话进行处理，返回更新时间最新的那条信息
	@SuppressWarnings("rawtypes")
	protected Map getValidDeep(List<Map> deeps) {
		Map newestDeep = deeps.get(0);
		for (Map deep : deeps) {
			if (deep.get("updatetime")
					.toString()
					.compareToIgnoreCase(
							newestDeep.get("updatetime").toString()) > 0) {
				newestDeep = deep;
			}
		}
		return newestDeep;
	}

	/**
	 * 考虑一个poiid对应多条rti的情况，此时需要对所有的rti进行组装； 把多组rti合并成一个json
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap combineRtis(LinkedHashMap combineMap, List<Map> ids,
			String cp) {
		if (ids == null || ids.size() == 0) {
			return combineMap;
		}
		String idSql = "id = '";
		for (Map id : ids) {
			idSql = idSql
					+ (idSql == "id = '" ? id.get("id") : " or id = '"
							+ id.get("id"));
			idSql += "'";
		}
		String sql = "select * from " + rti_table + " where cp = '" + cp
				+ "' and (" + idSql + ")";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rti = ddr.readAll();
		if (rti == null || rti.size() == 0) {
			combineMap.put("rti", null);
			return combineMap;
		}
		Map result = rtitask.toApiTranser(getValidRtis(rti));
		if (result == null || result.size() == 0) {
			combineMap.put("rti", null);
		} else {
			List<Map> rtisresult = new ArrayList<Map>();
			List<LinkedHashMap> idRtis = (List<LinkedHashMap>) JSON
					.parse(result.get("rti").toString());
			for (LinkedHashMap idRti : idRtis) {
				if (rtisresult != null && !rtisresult.contains(idRti)) {
					rtisresult.add(idRti);
				}
			}
			if (assertReview(rtisresult)) {
				rtisresult = getValidrtisresult(rtisresult);
			}
			combineMap.put("rti", rtisresult);
		}
		return combineMap;
	}

	/**
	 * 判断动态信息中是否有两条及其以上的评论，如果是的话，随机保留一条
	 */
	@SuppressWarnings("rawtypes")
	private List<Map> getValidrtisresult(List<Map> rtisresult) {
		List<Map> rtis = new ArrayList<Map>();
		for (Map rti : rtisresult) {
			Object market = rti.get("market");
			if (market != null && market.equals("review") && assertReview(rtis)) {
				continue;
			}
			if (rtis != null && !rtis.contains(rti)) {
				rtis.add(rti);
			}
		}
		return rtis;
	}

	/**
	 * 判断传入的里面是否含有评论信息,是的话返回true，否则返回false
	 */
	@SuppressWarnings({ "rawtypes" })
	private boolean assertReview(List<Map> rtis) {
		for (Map rti : rtis) {
			Object market = rti.get("market");
			if (market != null && market.equals("review")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 组装idDictionaries信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LinkedHashMap combineidDictionaries(LinkedHashMap combineMap,
			String cp) {
		Object id = ((Map) combineMap.get("from")).get("src_id");
		LinkedHashMap idDictionaries = new LinkedHashMap();
		idDictionaries.put(cp + "_id", id);

		// 针对携程，加入"hotel_ctrip_wireless_api_city_id" : "1",
		if ("hotel_ctrip_wireless_api".equalsIgnoreCase(cp)) {
			Object city = ((Map) combineMap.get("from")).get("city");
			idDictionaries.put(cp + "_city_id", city);
		}

		// 点评类统一处理，加入dianping_api字段
		if (cp.contains("dianping") && !cp.equals("dianping_refresh")) {
			idDictionaries.put("dianping_api_id", id);
		}
		combineMap.put("idDictionaries", idDictionaries);
		return combineMap;
	}

	/**
	 * 组装spec信息:有些cp不需要组装，放在各自的特殊处理中
	 */
	@SuppressWarnings("rawtypes")
	protected LinkedHashMap combineSpec(LinkedHashMap combineMap, String cp) {
		// 点评类全都统一处理，加入spec字段
		if (cp.contains("dianping") && !cp.equals("dianping_refresh")) {
			combineMap = getDianpingSpec(combineMap, cp);
		}
		combineSpecContent(combineMap, cp);
		return combineMap;
	}
	
	@SuppressWarnings("rawtypes")
	protected LinkedHashMap combineSpecContent(LinkedHashMap combineMap, String cp) {
		return combineMap;
	}

	/**
	 * 点评类统一处理，加入spec字段
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getDianpingSpec(LinkedHashMap combineMap, String cp) {
		// 取出对应的id
		Map from = (Map) combineMap.get("from");
		Object id = from.get("src_id");
		String sql = "select * from poi_deep where cp = '" + cp
				+ "' and id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> deeps = ddr.readAll();
		Object deepObj = deeps.get(0).get("deep");
		Map info = (Map) JSON.parse(deepObj + "");
		Object specinfo = info.get("spec");
		if (specinfo == null || specinfo.equals("")) {
			return combineMap;
		}
		Map spec = (Map) JSON.parse(specinfo.toString());
		for (Object key : spec.keySet()) {
			spec.put(key,
					spec.get(key) == null || spec.get(key).equals("") ? null
							: spec.get(key));
		}

		Map dianping = new HashMap();
		dianping.put("dianping_api", spec);

		combineMap.put("spec", dianping);
		return combineMap;
	}

	/**
	 * 获取有效可用的rti信息
	 */
	@SuppressWarnings("rawtypes")
	private List<Map> getValidRtis(List<Map> rtis) {
		List<Map> validRtis = new ArrayList<Map>();
		for (Map rti : rtis) {
			List<Map> temps = getValidRti(rti);
			for (Map temp : temps) {
				validRtis.add(SaveHelper.transferToSmall(temp));
			}
		}
		return validRtis;
	}

	/**
	 * 判断当前rti数组是否可用，不可用的删除，返回部分只包括可用的rti信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<Map> getValidRti(Map rti) {
		Object rtiInfo = rti.get("rti");
		List<Map> maps = new ArrayList<Map>();
		if (rtiInfo == null || rtiInfo.equals("null") || rtiInfo.equals("")) {
			return maps;
		}
		List<Map> rtiMaps = (List<Map>) JSON.parse(SaveHelper
				.getTranserJson(rtiInfo));
		for (Map rtiMap : rtiMaps) {
			rtiMap.put("cp", rti.get("cp"));
			rtiMap.put("id", rti.get("id"));
			maps.add(rtiMap);
		}
		return maps;
	}

	/**
	 * 根据传入的参数类型，选出该类型下有更新的poiid（深度或动态有一个更新都要进行更新），一次最多取出maxNum个
	 */
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	private List<Map> getUpdatePoiids(String cp, int maxNum) {
		Set<Map> poiidset = new HashSet<Map>();
		Set<Map> idset = new HashSet<Map>();
		String sql;
		DBDataReader ddr;
		// 从深度表中查找有变化的所有poiid和id
		sql = "select poiid,id from "
				+ deep_table
				+ " where cp = '"
				+ cp
				+ "' and "
				+ update_flag
				+ " = "
				+ templet.UPDATE
				+ " and poiid is not null and poiid != '' and poiid != 'null' limit :from,:size";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> dataMap = new ArrayList<Map>();
		do {
			dataMap = ddr.readList();
			for (Map data : dataMap) {
				Map id = new HashMap();
				id.put("id", data.get("id"));
				if (idset.size() == 0 || !idset.contains(id)) {
					idset.add(id);
				}

				Map poiid = new HashMap();
				Object poiidObj = data.get("poiid");
				if (poiidObj == null || poiidObj.equals("")
						|| poiidObj.equals("null")) {
					continue;
				}
				poiid.put("poiid", poiidObj);
				if (poiidset.size() == 0 || !poiidset.contains(poiid)) {
					poiidset.add(poiid);
				}
			}
		} while (!ddr.isFinished() && poiidset.size() < maxNum);

		// 再从动态表中查找有变化的当前cp的所有id
		sql = "select distinct id from " + rti_table + " where cp = '" + cp
				+ "' and " + update_flag + " = " + templet.UPDATE
				+ " limit :from,:size";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> ids = new ArrayList<Map>();
		do {
			ids = ddr.readList();
			// 根据cp和id在深度表中查找对应的poiid
			for (Map id : ids) {
				if (idset.contains(id)) {
					continue;
				}
				String sql1 = "select poiid from " + deep_table
						+ " where cp = '" + cp + "' and id = '" + id.get("id")
						+ "'";
				DBDataReader ddr1 = new DBDataReader(sql1);
				ddr1.setDbenv(null);
				List<Map> poiid = ddr1.readAll();
				if (poiid != null && poiid.size() > 0) {
					Object poiidObj = poiid.get(0).get("poiid");
					if (poiidObj == null || poiidObj.equals("")) {
						continue;
					} else if (poiidObj.toString().equalsIgnoreCase("null")) {
						continue;
					} else {
						if (!poiidset.contains(poiidObj)) {
							poiidset.add(poiid.get(0));
						}
					}
				}
			}
		} while (!ddr.isFinished() && poiidset.size() < maxNum);

		List<Map> poiids = new ArrayList<Map>();
		for (Map poiid : poiidset) {
			poiids.add(poiid);
		}

		log.info("本批次共取poiid的个数为：" + poiids.size());
		return poiids;
	}

	/**
	 * 根据传入的cp和poiid获取深度表中所有的id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map> getIdsFromPoiid(Object poiid, String cp) {
		String sql = "SELECT id FROM " + deep_table + " WHERE poiid = '"
				+ poiid + "' and cp = '" + cp + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readAll();
	}

	/**
	 * 经过save接口处理之后，把对应信息的标记位置为0;
	 */
	@SuppressWarnings({ "rawtypes", "static-access" })
	private void setUpdateFlag(Object poiid, List<Map> ids, String cp) {
		String sql = "update " + deep_table + " set " + update_flag + " = "
				+ templet.KEEP + ",updatetime = '" + SaveHelper.getUpdateTime()
				+ "' where cp = '" + cp + "' and poiid = '" + poiid + "'";
		sqlList.add(sql);
		// 如果动态信息存在，则对应更新动态表
		for (Map idm : ids) {
			Object id = idm.get("id");
			if (assertRtiExist(id, cp)) {
				sql = "update " + rti_table + " set " + update_flag + " = "
						+ templet.KEEP + ",updatetime = '"
						+ SaveHelper.getUpdateTime() + "' where cp = '" + cp
						+ "' and id = '" + id + "'";
				sqlList.add(sql);
			}
		}
	}

	/**
	 * 对于新增暂时不入库的poiid，把对应信息的标记位置为-1;
	 */
	@SuppressWarnings({ "rawtypes" })
	private void setUpdateFlagForAdd(Object poiid, List<Map> ids, String cp) {
		String sql = "update " + deep_table + " set " + update_flag
				+ " = -1, updatetime = '" + SaveHelper.getUpdateTime()
				+ "' where cp = '" + cp + "' and poiid = '" + poiid + "'";
		sqlList.add(sql);
		// 如果动态信息存在，则对应更新动态表
		for (Map idm : ids) {
			Object id = idm.get("id");
			if (assertRtiExist(id, cp)) {
				sql = "update " + rti_table + " set " + update_flag
						+ " = -1, updatetime = '" + SaveHelper.getUpdateTime()
						+ "' where cp = '" + cp + "' and id = '" + id + "'";
				sqlList.add(sql);
			}
		}
	}

	/**
	 * 进行纠错处理后，更新表中数据
	 */
	@SuppressWarnings("static-access")
	private void setUpdateFlag(Object poiid, String id, String cp) {
		String sql = "update " + error_table + " set " + update_flag + " = "
				+ templet.KEEP + ",updatetime = '" + SaveHelper.getUpdateTime()
				+ "' where cp = '" + cp + "' and id = '" + id + "'";
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		dbexec.setSql(sql);
		dbexec.dbExec();
	}

	/**
	 * 根据传入的id，判断当前cp下是否有该条动态信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean assertRtiExist(Object id, String cp) {
		String sql = "SELECT * FROM " + rti_table + " WHERE cp = '" + cp
				+ "' AND id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rtiinfo = ddr.readAll();
		if (rtiinfo == null || rtiinfo.size() < 1) {
			return false;
		}
		return true;
	}
	
	/**
	 * 加载新的匹配关系（用于修补融合问题）并初始化hasNewPoiidInfo标记位
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initNewpoiidInfos(String cp) {
		String sql = "select * from "
				+ newpoi_table
				+ " where cp = '"
				+ cp
				+ "' limit :from,:size";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> datas = new ArrayList<Map>();
		do {
			datas = ddr.readList();
			for(Map data : datas){
				Object newpoiid = data.get("newpoiid");
				Object id = data.get("id");
				if(assertNull(id) || assertNull(newpoiid)){
					continue;
				}
				newpoiidInfos.put(cp + "&" + id, newpoiid);
			}
		} while (!ddr.isFinished());
		
		if(newpoiidInfos != null && newpoiidInfos.size() > 0){
			hasNewPoiidInfo = true;
		}
	}
	
	/**
	 * 判断传入参数是否为空，是的话，返回true
	 */
	private boolean assertNull(Object str){
		if (str == null || str.toString().equals("") || str.toString().equalsIgnoreCase("null")){
			return true;
		}
		
		return false;
	}
}
