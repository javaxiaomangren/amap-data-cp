/**
 * 2013-8-2
 */
package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;

public class ElongMatch {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		// 定义输出文件
		String Path = "E:/dis0.txt";
		File f = new File(Path);
		try {
			f.createNewFile();
			System.out.println(f.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write("\n");

		//读取现在数据
		String nowPath = "E:/elong.csv";
		BufferedReader nowreader = new BufferedReader(new InputStreamReader(
				new FileInputStream(nowPath), "GBK"));
		String now;
		nowreader.readLine();
		
		while ((now = nowreader.readLine()) != null) {
			String[] nowFileds = now.split(",");
			String nowPoiid = nowFileds[1];
			String nowName = nowFileds[2];
			String nowId = nowFileds[0];
			
			String sql = "select * from elong_chongfu_chaxun where poiid = '" + nowPoiid + "' and HotelName = '" + nowName + "' and HotelId like '%" + nowId + "'";
			DBDataReader ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> dataMap = ddr.readAll();
			if(dataMap != null && dataMap.size() > 0){
				Object id = dataMap.get(0).get("HotelId");
				writer.write(id + "");
				writer.write("\n");
				writer.flush();
			}else{
				writer.write("\n");
				writer.flush();
			}
		}
		writer.close();
	}
}
