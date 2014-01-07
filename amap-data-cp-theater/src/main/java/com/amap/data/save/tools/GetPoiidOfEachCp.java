/**
 * 2013-11-5
 */
package com.amap.data.save.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;

/**
 * 获取数据库中每个cp有效的poiid列表
 */
public class GetPoiidOfEachCp {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws IOException {
		String sql = "SELECT DISTINCT cp FROM poi_deep";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> cps = ddr.readAll();

		for (Map cpm : cps) {
			Object cp = cpm.get("cp");
			//定义输出文件
			String path = "E:/lunhui/" + cp + "_poiid.csv";
			File f0 = new File(path);
			try {
				f0.createNewFile();
				System.out.println(f0.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(f0), "gbk");
			
			// 有poiid的
			sql = "SELECT poiid FROM poi_deep WHERE cp = '"
					+ cp
					+ "' AND poiid IS NOT NULL AND poiid != '' AND poiid != 'null' and deep NOT LIKE '%\"status\":\"-1\"%' AND deep NOT LIKE '%\"status\":-1%' AND deep NOT LIKE '%\"STATUS\":\"-1\"%' and deep not like '%\"status\" : \"-1\"%' limit :from,:size";
			ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> dataMap = new ArrayList<Map>();
			do {
				dataMap = ddr.readList();
				for (Map data : dataMap) {
					Object poiid = data.get("poiid");
					writer.write(poiid + "");
					writer.write("\n");
				}
			} while (!ddr.isFinished());
			writer.close();
		}
	}
}
