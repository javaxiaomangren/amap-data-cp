/**
 * 2013-7-9
 */
package com.amap.theater.staticnum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * 大麦网新抓取数据经过匹配 可能有一部分和原来相比 无法匹配上，找出之前有匹配而现在没有匹配的数据
 */
public class AssertPoiid {

	public static void main(String[] args) throws IOException{
		String dataPath = "E:/now.csv";

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataPath), "gb2312"));
		String dataStr = null;

		//先把现在有poiid的所有数据存入poiidHasDeal
		Set<Object> poiidHasDeal = new HashSet<Object>();;
		while ((dataStr = reader.readLine()) != null) {
			if(poiidHasDeal.size() > 0 && poiidHasDeal.contains(dataStr)){
				continue;
			}
			poiidHasDeal.add(dataStr);
		}
		
		//读取线上的所有poiid，找出不在set中的所有poiid
		dataPath = "E:/theater.csv";
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataPath), "gb2312"));
		while ((dataStr = reader.readLine()) != null) {
			if(!poiidHasDeal.contains(dataStr)){
				System.out.println(dataStr);
			}
		}
	}
}
