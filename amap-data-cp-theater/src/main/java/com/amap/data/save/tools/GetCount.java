/**
 * 2013-11-5
 */
package com.amap.data.save.tools;

import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;

/**
 * 获取数据库中每个cp有效的poiid个数
 */
public class GetCount {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args){
		String sql = "SELECT DISTINCT cp FROM poi_deep";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> cps = ddr.readAll();
		
		for(Map cpm : cps){
			Object cp = cpm.get("cp");
			//有poiid的
//			sql = "SELECT COUNT(*) FROM poi_deep WHERE cp = '" + cp + "' AND poiid IS NOT NULL AND poiid != '' AND poiid != 'null' and deep NOT LIKE '%\"status\":\"-1\"%' AND deep NOT LIKE '%\"status\":-1%' AND deep NOT LIKE '%\"STATUS\":\"-1\"%' and deep not like '%\"status\" : \"-1\"%'";
			//总共的：
//			sql = "SELECT COUNT(*) FROM poi_deep WHERE cp = '" + cp + "' AND deep NOT LIKE '%\"status\":\"-1\"%' AND deep NOT LIKE '%\"status\":-1%' AND deep NOT LIKE '%\"STATUS\":\"-1\"%' and deep not like '%\"status\" : \"-1\"%'";
			//点评数据个数
			if(cp == null || !cp.toString().contains("dianping")){
				continue;
			}
			sql = "SELECT COUNT(*) FROM poi_deep WHERE cp = '" + cp + "' AND poiid IS NOT NULL AND poiid != '' AND poiid != 'null'";
			ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> counts = ddr.readAll();
			if(counts == null || counts.size() != 1){
				System.out.println(cp + ";");
			}else {
				System.out.println(cp + ":" + counts.get(0).get("count(*)"));
			}
		}
	}
}
