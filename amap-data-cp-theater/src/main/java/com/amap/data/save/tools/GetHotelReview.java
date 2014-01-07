/**
 * 2013-7-23
 */
package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetHotelReview {
	public static void main(String[] args) throws IOException{
		// 输出信息
		String path = "E:/elong.txt";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f0));
		
		String[] result = getRti();
		for(int i = 0; i < result.length; i++){
			String rti = result[i];
			
			String json = JsonTool.formatJson(rti, "  ");
			writer.write("/* " + i + " */");
			writer.write("\n");
			writer.write(json);
			writer.write("\n");
			writer.write("\n");
		}
		writer.close();
	}
	
	//从txt中读取各条信息
	//get poiid
		private static String[] getRti() throws IOException{
			String fullDataPath = "E:/rtis.txt";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullDataPath)));
			String data;
			
			String[] result = new String[100];
			int i = 0;
			while ((data = reader.readLine()) != null) {
				result[i] = data;
				i++;
			}
			
			return result;
		}
}
