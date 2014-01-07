/**
 * 2013-7-1
 */
package com.amap.theater.crawl.autoupdatedeep;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.data.WriteToDB;
import com.amap.base.http.HttpclientUtil;
import com.amap.data.base.TempletConfig;
import com.amap.theater.crawl.Crawl;
import com.amap.theater.insertMiddleTable.TheaterInsertMiddleTable;
import com.amap.theater.match.GetMatchPoiid;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 定期更新深度信息
 */
public class UpdateDeep {
	private static final Logger log = LoggerFactory.getLogger(UpdateDeep.class);

	@SuppressWarnings("rawtypes")
	public void run() throws UnsupportedEncodingException {
		try {
			// 首先获取全量深度数据
			List<Map> dataList = getUpdateDeeps();

			// 从中间表中判断，原来是否是已经匹配上的数据，是的话，赋值poiid
			dataList = getPoiid(dataList);
//			dataList = getPoiidFromDeep(dataList);

			// 针对深度数据，如果没有poiid，则调用匹配接口获取其poiid
			dataList = getPoiidFroUpdateDeeps(dataList);
			
			//获取到poiid，统一把poiid赋值给base中的poiid
			dataList = dealBase(dataList);

			// 保存：先对应修改中间表，再入deep表
			saveData(dataList);
		} catch (Exception e) {
			log.info("程序异常，错误原因是：" + e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws UnsupportedEncodingException {
		try {
			// 首先获取全量深度数据
			List<Map> dataList = getUpdateDeeps();

			// 从中间表中判断，原来是否是已经匹配上的数据，是的话，赋值poiid
			dataList = getPoiid(dataList);
//			dataList = getPoiidFromDeep(dataList);

			// 针对深度数据，如果没有poiid，则调用匹配接口获取其poiid
			dataList = getPoiidFroUpdateDeeps(dataList);
			
			//获取到poiid，统一把poiid赋值给base中的poiid
			dataList = dealBase(dataList);

			// 保存：先对应修改中间表，再入deep表
			saveData(dataList);
		} catch (Exception e) {
			log.info("程序异常，错误原因是：" + e);
		}
	}
	
	/**
	 * 把poiid赋值给base中的poiid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> dealBase(List<Map> dataList){
		List<Map> datas = new ArrayList<Map>();
		
		for(Map data : dataList){
			Object poiid = data.get("poiid");
			if(poiid != null && !poiid.equals("") && !poiid.equals("null")){
				Object baseObj = data.get("base");
				if(baseObj != null && !baseObj.equals("") && !baseObj.equals("null")){
					LinkedHashMap base = null;
					try{
						base = (LinkedHashMap) JSON.parse(baseObj + "");
						base.put("poiid", poiid);
						data.put("base", JSON.serialize(base));
					}catch (Exception e) {
					}
				}
			}
			datas.add(data);
		}
		return datas;
	}

	/**
	 * 从中间表中获取对应的poiid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getPoiid(List<Map> dataList) {
		List<Map> datas = new ArrayList<Map>();
		for (Map data : dataList) {
			Object id = data.get("id");
			String sql = "select * from theatre_damai where id = '" + id + "'";
			DBDataReader ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> result = ddr.readAll();
			if (result != null && result.size() > 0) {
				data.put("poiid", result.get(0).get("poiid"));
				data.put("type", result.get(0).get("type"));
				data.put("dismatch_flag", result.get(0).get("dismatch_flag"));
			} else {
				data.put("dismatch_flag", 0);
			}
			datas.add(data);
		}
		return datas;
	}
	
	/**
	 * 从deep表中获取poiid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getPoiidFromDeep(List<Map> dataList) {
		String url = "http://10.2.134.64:8089/getPoiid/GetPoiid";
		List<Map> datas = new ArrayList<Map>();
		for(Map data : dataList){
			Map temp = new HashMap();
			temp.put("cp", "theater_damai_api");
			temp.put("cpid", data.get("id").toString());
			
			Object poiid = HttpclientUtil.get(url, temp);
			data.put("poiid", poiid);
			datas.add(data);
		}
		return datas;
	}
	

	/**
	 * 从深度接口中全量抓取数据，并返回获取到的全量数据
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Map> getUpdateDeeps() throws IOException {
		String s = "theater_damai_api_deep_1";
		TempletConfig templet = new TempletConfig(s);
		List<Map> dataList = new Crawl(templet).getDeeps();

		return dataList;
	}

	/**
	 * 针对更新的数据，调用匹配接口获取poiid
	 *
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map> getPoiidFroUpdateDeeps(List<Map> dataList)
			throws UnsupportedEncodingException {
		List<Map> datas = new ArrayList<Map>();
		int count = 0;
		for (Map data : dataList) {
			count++;
			data.put("cp", "theater_damai_api");
			Object dismatch_flag = data.get("dismatch_flag");
			//如果当前数据之前是错误匹配的，则不参与匹配
			if(Integer.parseInt(dismatch_flag.toString()) == 1){
				continue;
			}

			Object poiidObj = data.get("poiid");

			// 如果为空，先从mongo中获取poiid，如果有值的话，返回原来匹配的值
			if (poiidObj == null || poiidObj.equals("null")
					|| poiidObj.equals("") || poiidObj.equals("NULL")) {
				data = GetMatchPoiid.getMapContainsPoiid(data);
			}

			if (count % 100 == 0) {
				log.info("已经匹配的数据个数为：" + count);
			}
			datas.add(data);
		}
		return datas;
	}

	/**
	 * 保存数据：先把数据对应存入中间表，然后再写入deep表
	 */
	@SuppressWarnings("rawtypes")
	private static void saveData(List<Map> dataList) {
		// 数据存入中间表：包括更新和新增
		updateExisDeeps(dataList);

		// 写入deep表:原来抓取的已经下线的深度信息也需要处理
		// 获取已经下线的深度信息：中间表中的标记位为0
		List<Map> offDatas = getOffDeeps();

		// 把更新、新增、下线深度信息都写入中间表
		if (dataList != null && dataList.size() > 0) {
			TheaterInsertMiddleTable.deepInsert(dataList);
		}
		if (offDatas != null && offDatas.size() > 0) {

			TheaterInsertMiddleTable.deepInsert(offDatas);
		}

		// 把表中过期的深度数据删除，防止下次重复统计
		deleteOverData(offDatas);
	}

	/**
	 * 删除深度表中的过期数据
	 */
	@SuppressWarnings("rawtypes")
	private static void deleteOverData(List<Map> offDatas) {
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		List<String> sqlList = new ArrayList<String>();

		for (Map offdata : offDatas) {
			Object id = offdata.get("id");
			String sql = "delete from theatre_damai where id = '" + id + "'";
			sqlList.add(sql);
		}
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();

		log.info("从中间表中删除已经下线深度数据，个数为：" + sqlList.size());
	}

	/**
	 * 获取已经下线的深度信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getOffDeeps() {
		String sql = "select * from theatre_damai where update_flag = 0";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> offDatas = ddr.readAll();

		List<Map> datas = new ArrayList<Map>();
		int count = 0;
		for (Map data : offDatas) {
			data.put("status", "-1");
			datas.add(data);

			Object poiid = data.get("poiid");
			if (poiid != null && !poiid.equals("") && !poiid.equals("null")
					&& !poiid.equals("(NULL)")) {
				count++;
			}
		}
		log.info("下线的深度信息个数为：" + datas.size());
		log.info("下线的深度信息中有poiid的个数为：" + count);
		return datas;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void updateExisDeeps(List<Map> dataList) {
		// 首先从接口读取最新的场馆信息
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		// 判断读取的静态数据在深度表中是否存在
		List<String> sqlList = new ArrayList<String>();
		List<Map> addList = new ArrayList();
		int updateMatchNum = 0;
		int addMatchNum = 0;
		for (Map deep : dataList) {
			Object id = deep.get("id");
			String sql0 = "select * from theatre_damai where id = '" + id + "'";
			DBDataReader ddr = new DBDataReader(sql0);
			ddr.setDbenv(null);
			List<Map> datas = ddr.readList();
			String sql = null;
			// 处理test
			if (deep.get("text") != null && !deep.get("text").equals("")) {
				deep.put("text", delHTMLTag(deep.get("text").toString()));
			}
			if (deep.get("Text") != null && !deep.get("Text").equals("")) {
				deep.put("Text", delHTMLTag(deep.get("Text").toString()));
			}
			boolean matchFlag = assertMatch(deep);
			if (datas.size() != 0) {
				// 原表中有该条poi，进行更新,先删除原来数据，再插入
				sql = "delete from theatre_damai where id = '" + id + "'";
				sqlList.add(sql);
				addList.add(deep);
				if (matchFlag) {
					updateMatchNum++;
				}
			} else {
				// 新增信息
				deep = changepoiid(deep);
				addList.add(deep);
				if (matchFlag) {
					addMatchNum++;
				}
			} 
		}
		log.info("需要更新的深度信息个数为：" + sqlList.size());
		log.info("需要更新的深度信息中，匹配上的个数为：" + updateMatchNum);
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();

		// 输出统计用的信息
		log.info("目前总共有的深度信息为：" + dataList.size());
		log.info("新增的深度信息为：" + (dataList.size() - sqlList.size()));
		log.info("新增的深度信息中，匹配上的个数为：" + addMatchNum);

		for (Map data : dataList) {
			try {
				new WriteToDB().toDBSingle("theatre_damai", data);
			} catch (Exception e) {
				System.out.println(JSON.serialize(data));
			}
		}
		// new WriteToDB().toDB("theatre_damai", dataList);
	}
	
	/**
	 * 新增信息判断是否在错误表中存在，如果存在的话，判断其poiid是否和当前的相同，如果相同，则poiid置空
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map changepoiid(Map deep){
		//获取当前信息的id和poiid信息
		Object id = deep.get("id");
		Object poiid = deep.get("poiid");
		if(poiid == null || poiid .equals("") || poiid.equals("null")){
			return null;
		}
		
		//从错误表中读取对应的信息，看是否能找到
		String sql = "select * from poi_error where cp = 'theater_damai_api' and id = '" + id + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> result = ddr.readAll();
		if(result == null || result.size() == 0){
			//当前信息不在错误表中
			return deep;
		}
		Object orgPoiid = result.get(0).get("poiid");
		if(poiid.equals(orgPoiid)){
			//如果目前匹配上的和错误表中的一致，则清空poiid
			deep.put("poiid", null);
		}
		return deep;
	}

	/**
	 * 判断当前深度信息是否有poiid
	 */
	@SuppressWarnings("rawtypes")
	private static boolean assertMatch(Map deep) {
		if (deep.get("poiid") == null || deep.get("poiid").equals("")
				|| deep.get("poiid").toString().equalsIgnoreCase("null")) {
			return false;
		}
		return true;
	}

	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		htmlStr = htmlStr.replaceAll("&nbsp;", "");
		htmlStr = htmlStr.replace("?nbsp;", "");

		Pattern p_script = Pattern.compile(regEx_script,
				Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern
				.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		htmlStr = HtmlUtils.htmlUnescape(htmlStr);

		htmlStr = htmlStr.replace("null", "");
		htmlStr = htmlStr.replaceAll("&nbsp;", "");
		htmlStr = htmlStr.replaceAll(
				"(&lt;(?i)(|)[^>]*/?&gt;)|(&nbsp;)|(<[^>]*>)", "");

		// 替换类似&#8221;
		String regEx = "&#\\d{2,};";
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(htmlStr);
		htmlStr = m.replaceAll(""); // 过滤html标签

		if (htmlStr.endsWith("<font color")) {
			htmlStr = htmlStr.replace("<font color", "");
		}

		return htmlStr.trim(); // 返回文本字符串
	}
}
