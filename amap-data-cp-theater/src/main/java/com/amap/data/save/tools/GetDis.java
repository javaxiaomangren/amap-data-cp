/**
 * 2013-7-31
 */
package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import zengkunceju.ProjectionUtil;

public class GetDis {
	public static void main(String[] args) throws IOException {
		ProjectionUtil pUtil = new ProjectionUtil();

		// 定义输出文件
		String Path = "E:/dis.csv";
		File f = new File(Path);
		try {
			f.createNewFile();
			System.out.println(f.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write("\n");

		// 读取数据
		String cdataPath = "E:/error_poimatch.csv";
		BufferedReader creader = new BufferedReader(new InputStreamReader(
				new FileInputStream(cdataPath), "GBK"));
		String cdataStr;
		creader.readLine();
		
		while ((cdataStr = creader.readLine()) != null) {
			String[] fields = cdataStr.split(",");
			int distance = 0;
			try {
				int x1num = 5;
				int y1num = 6;
				int x2num = 10;
				int y2num = 11;
				if(fields[1] == null || fields[1].equals("")){
					writer.write("\n");
					continue;
				}
				if (fields[x1num] == null || fields[y1num] == null
						|| fields[x2num] == null || fields[y2num] == null || 
						fields[x1num].equalsIgnoreCase("null") || fields[y1num].equalsIgnoreCase("null")
						|| fields[x2num].equalsIgnoreCase("null") || fields[y2num].equalsIgnoreCase("null")) {
					distance = 0;
				} else {
					double x1 = Double.parseDouble(fields[x1num]);
					double y1 = Double.parseDouble(fields[y1num]);

					double x2 = Double.parseDouble(fields[x2num]);
					double y2 = Double.parseDouble(fields[y2num]);

					distance = pUtil.ComputeFormCD(x1, y1, x2, y2);
				}
				writer.write(distance + "");
				writer.write("\n");
				writer.flush();
			} catch (Exception e) {
				System.out.println(fields);
				writer.write("\n");
				writer.flush();
			}

		}
		writer.close();
	}
}
