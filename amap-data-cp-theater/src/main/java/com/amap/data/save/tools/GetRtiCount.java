/**
 * 2013-11-5
 */
package com.amap.data.save.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.mongodb.util.JSON;

/**
 * 获取数据库中每个cp有效的poiid个数
 */
public class GetRtiCount {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args){
		String sql = "SELECT DISTINCT cp FROM poi_deep";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> cps = ddr.readAll();
		int total_count = 0;
		for(Map cpm : cps){
			int num = 0;
			Object cp = cpm.get("cp");
			if(cp != null && cp.toString().contains("dianping")){
				sql = "select * from poi_rti where cp = '" + cp + "' and (update_flag = 0 or test_update_flag = 0) limit :from,:size";
				ddr = new DBDataReader(sql);
				ddr.setDbenv(null);
				List<Map> datas = new ArrayList<Map>();
				do {
					datas = ddr.readList();
					for(Map data : datas){
						Object rti = data.get("rti");
						if(rti != null && rti.toString().contains("groupbuy")
								&& !rti.toString().contains("\"groupbuy_num\": 0")){
							List<Map> rtis = (List<Map>) JSON.parse(rti.toString());
							for(Map rtim : rtis){
								if(rtim.containsKey("groupbuy_num") && rtim.get("groupbuy_num") != null && !"".equals(rtim.get("groupbuy_num"))){
									num += Integer.parseInt(rtim.get("groupbuy_num").toString());
								}
							}
						}
					}
				}while (!ddr.isFinished());
				System.out.println("当前cp是：" + cp + ",其团购个数为：" + num);
				total_count += num;
			}
		}
		System.out.println("点评类的团购总个数为：" + total_count);
	}
}
