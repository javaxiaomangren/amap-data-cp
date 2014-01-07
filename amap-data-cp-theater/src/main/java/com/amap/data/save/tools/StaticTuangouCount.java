/**
 * 2013-12-5
 */
package com.amap.data.save.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;

/**
 * 统计有团购信息的poiid个数
 */
public class StaticTuangouCount {
	private static String cp = "groupbuy_meituan_api";
	@SuppressWarnings("rawtypes")
	private static Map poiids = new HashMap();

	// 找到团购中所有的poiid下挂的id信息
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void findPoiids() {
		String sql = "SELECT * FROM poi_deep WHERE cp = '"
				+ cp
				+ "' AND poiid IS NOT NULL AND poiid != '' AND poiid != 'null' AND deep NOT LIKE '%\"status\":\"-1\"%' and update_flag != -1 limit :from,:size";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> dataMap = new ArrayList<Map>();
		do {
			dataMap = ddr.readList();
			for(Map data : dataMap){
				Object poiid = data.get("poiid");
				Object id = data.get("id");
				
				List<String> ids = new ArrayList<String>();
				if(poiids != null && poiids.containsKey(poiid)){
					ids = (List<String>) poiids.get(poiid);
					ids.add(id.toString());
				} else {
					ids.add(id.toString());
				}
				poiids.put(poiid, ids);
			}
		} while (!ddr.isFinished());
		System.out.println("poiid加载完毕，共有poiid的个数为：" + poiids.size());
	}
	
	/**
	 * 统计有动态信息的个数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void findCount(){
		int count = 0;
		int i = 0;
		for(Object poiid : poiids.keySet()){
			i++;
			if(i % 1000 == 0){
				System.out.println("已经处理的poiid个数为：" + i);
			}
			List<String> ids = (List<String>) poiids.get(poiid);
			for(String id : ids){
				String sql = "select * from poi_rti where cp = '" + cp + "' and id = '" + id + "'";
				DBDataReader ddr = new DBDataReader(sql);
				ddr.setDbenv(null);
				List<Map> rtis = ddr.readAll();
				if(rtis == null || rtis.size() == 0){
					continue;
				}
				Object rti = rtis.get(0).get("rti");
				if(rti != null && !rti.equals("") && (rti.toString().contains("GROUP_ID") || rti.toString().contains("group_id"))){
					count++;
					break;
				}
			}
		}
		System.out.println("有动态信息的poiid个数为：" + count);
	}
	
	public static void main(String[] args){
		findPoiids();
		findCount();
	}
}
