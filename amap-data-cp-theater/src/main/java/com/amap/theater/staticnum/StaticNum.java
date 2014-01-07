/**
 * 2013-6-19
 */
package com.amap.theater.staticnum;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

import java.util.*;

/**
 * 统计大麦网更新个数，包括有poiid和无poiid的
 */
public class StaticNum {
	private static String beginTime = "2013-08-31";
	protected final static String deep_table = "poi_deep";
	protected final static String rti_table = "poi_rti";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception{
		//统计一周内更新个数
		//首先统计一周内动态信息更新总个数
		String sql = "SELECT * FROM theatre_rti WHERE updatetime > '" + beginTime + "' and state != 4";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rtiDatas = ddr.readAll();
		System.out.println("总共新增的动态信息个数为：" + rtiDatas.size());
		
		//统计一周内动态信息下线个数
		sql = "SELECT * FROM theatre_rti WHERE updatetime > '" + beginTime + "' and state = 4";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		rtiDatas = ddr.readAll();
		System.out.println("总共下线的动态信息个数为：" + rtiDatas.size());
		
		//影响的poiid个数
		sql = "SELECT distinct poiid FROM " + deep_table + " WHERE cp = 'theater_damai_api' and updatetime > '" + beginTime + "' and poiid != '' and poiid != 'null'";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		rtiDatas = ddr.readAll();
		System.out.println("总共涉及到的poiid个数为：" + rtiDatas.size());
		
		//有更新但是没有poiid的动态信息个数
		sql = "SELECT * FROM " + rti_table + " WHERE cp = 'theater_damai_api' and updatetime > '" + beginTime + "' and update_flag = 1";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		rtiDatas = ddr.readAll();
		System.out.println("没有poiid的动态信息个数为：" + rtiDatas.size());
		
		//新增影响到的poiid个数
		String url = "http://10.2.134.64:8089/getPoiid/GetPoiid";
		Map m = new HashMap();
		m.put("cp", "theater_damai_api");
		
		//新增所有的id信息
		Set<Object> poiids = new HashSet<Object>();
		int count = 0;
		int havePoiidCount = 0;
		int noPoiidCount = 0;
		sql = "SELECT distinct id FROM theatre_rti WHERE updatetime > '" + beginTime + "' and state != 4";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		rtiDatas = ddr.readAll();
		for(Map rtiData : rtiDatas){
			m.put("cpid", rtiData.get("id"));
			String json = JSON.serialize(m);
			
			Map mm = new HashMap();
			mm.put("json", json);
			String result = null;
			try {
				result = HttpclientUtil.post(url, m, "UTF-8");
			} catch (Exception e) {
			}
			if(result == null || result.equals("")){
				//没有poiid的个数
				count++;
				noPoiidCount += getRtiNum(rtiData.get("id").toString(), true);
			}else if(!poiids.contains(result)){
				//有poiid的个数
				poiids.add(result);
				havePoiidCount += getRtiNum(rtiData.get("id").toString(), true);
			}else{
				havePoiidCount += getRtiNum(rtiData.get("id").toString(), true);
				continue;
			}
		}
		System.out.println("新增动态信息影响到的poiid个数为：" + poiids.size());
		System.out.println("有poiid新增动态信息个数为：" + havePoiidCount);
		System.out.println("新增动态信息没有poiid的个数为：" + count);
		System.out.println("没有poiid新增动态信息个数为：" + noPoiidCount);
		
		//下线信息
		poiids = new HashSet<Object>();
		count = 0;
		havePoiidCount = 0;
		noPoiidCount = 0;
		sql = "SELECT distinct id FROM theatre_rti WHERE updatetime > '" + beginTime + "' and state = 4";
		ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		rtiDatas = ddr.readAll();
		for(Map rtiData : rtiDatas){
			m.put("cpid", rtiData.get("id"));
			String json = JSON.serialize(m);
			
			Map mm = new HashMap();
			mm.put("json", json);
			String result = HttpclientUtil.post(url, m, "UTF-8");
			if(result == null || result.equals("")){
				count++;
				noPoiidCount += getRtiNum(rtiData.get("id").toString(), false);
			}else if(!poiids.contains(result)){
				poiids.add(result);
				havePoiidCount += getRtiNum(rtiData.get("id").toString(), false);
			}else{
				havePoiidCount += getRtiNum(rtiData.get("id").toString(), false);
				continue;
			}
		}
		System.out.println("下线动态信息影响到的poiid个数为：" + poiids.size());
		System.out.println("有poiid下线动态信息个数为：" + havePoiidCount);
		System.out.println("下线动态信息没有poiid的个数为：" + count);
		System.out.println("没有poiid下线动态信息个数为：" + noPoiidCount);
	}
	
	//根据传入的id，统计该id下新增的动态信息个数
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int getRtiNum(String id, boolean valid){
		String sql = "SELECT * FROM theatre_rti WHERE updatetime > '" + beginTime + "' and state != 4 and id = '" + id + "'";
		if(!valid){
			sql = "SELECT * FROM theatre_rti WHERE updatetime > '" + beginTime + "' and state = 4 and id = '" + id + "'";
		}
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> rtiDatas = ddr.readAll();
		if(rtiDatas == null){
			return 0;
		}
		return rtiDatas.size();
	}
}
