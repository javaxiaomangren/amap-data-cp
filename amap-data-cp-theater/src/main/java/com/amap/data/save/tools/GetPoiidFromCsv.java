/**
 * 2013-11-6
 */
package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;

/**
 * 根据csv文件中的cp和id，在库中查找对应的poiid
 */
public class GetPoiidFromCsv {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		// 输出信息
		String path = "E:/ctrip_poiid.csv";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");

		String fullDataPath = "E://poiid.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fullDataPath)));
		String data;
		int i = 0;
		int count = 0;
		String cp = "hotel_elong_api";
		while ((data = reader.readLine()) != null) {
			i ++;
			if(i % 1000 == 0){
				System.out.println("已经成功处理：" + i);
			}
			String id = data;
			String sql = "SELECT id FROM poi_deep where cp = '" + cp + "' and poiid = '" + id + "'";
			DBDataReader ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> poiids = ddr.readAll();
			if(poiids != null && poiids.size() == 1){
				Object poiid = poiids.get(0).get("id");
				writer.write(id + "," + poiid + ",");
				count++;
			} else {
				writer.write(id + ",");
			}
			writer.write("\n");
			writer.flush();
		}
		writer.close();
		System.out.println("找到poiid共有：" + count);
	}
}
