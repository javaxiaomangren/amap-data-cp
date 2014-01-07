/**
 * 2013-7-25
 */
package com.amap.theater.staticnum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class CompareCsv {
	public static void main(String[] args) throws IOException{
		String dataPath = "E:/mypoiid.csv";

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataPath), "gb2312"));
		String dataStr = null;
		//先把现在有poiid的所有数据存入poiidHasDeal
				Set<Object> poiid = new HashSet<Object>();;
				while ((dataStr = reader.readLine()) != null) {
					poiid.add(dataStr);
				}
				
				//读取另一个文件
				dataPath = "E:/poiid.csv";

				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(dataPath), "gb2312"));
				//先把现在有poiid的所有数据存入poiidHasDeal
						while ((dataStr = reader.readLine()) != null) {
							if(!poiid.contains(dataStr)){
								System.out.println(dataStr);
							}
						}
	}
}
