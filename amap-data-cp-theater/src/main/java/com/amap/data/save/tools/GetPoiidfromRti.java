/**
 * 2013-12-10
 */
package com.amap.data.save.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amap.base.data.DBDataReader;

public class GetPoiidfromRti {
	/**
	 * 根据传入的参数类型，选出该类型下有更新的poiid（深度或动态有一个更新都要进行更新），一次最多取出maxNum个
	 * @throws java.io.IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void getUpdatePoiids(String cp)
			throws IOException {
		// 输出信息
		String path = "E:/tuan_poiid.csv";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");

		Set<Map> poiidset = new HashSet<Map>();
		Set<Map> idset = new HashSet<Map>();
		String sql;
		DBDataReader ddr;

		// 再从动态表中查找有变化的当前cp的所有id
		sql = "select distinct id from poi_rti where cp = '" + cp
				+ "' and update_flag = 1 and updatetime > '2013-12-09 15:00:35' limit :from,:size";
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
				String sql1 = "select poiid from poi_deep where cp = '" + cp
						+ "' and id = '" + id.get("id") + "'";
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
		} while (!ddr.isFinished());

		List<Map> poiids = new ArrayList<Map>();
		for (Map poiid : poiidset) {
			poiids.add(poiid);
			writer.write(poiid.get("poiid") + "");
			writer.write("\n");
		}
		System.out.println(poiidset.size());
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		String cp = "dining_dianping_api";

		getUpdatePoiids(cp);
	}
}
