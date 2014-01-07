/**
 * 2013-8-5
 */
package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * 两个文件A和B，找出A中有而B中没有的
 */
public class GetDifferent {
	private static Set<String> a = new HashSet<String>();
	private static Set<String> b = new HashSet<String>();
	
	public static void main(String[] args) throws IOException {
		// 定义输出文件
		String Path = "E:/BhasBno.csv";
		File f = new File(Path);
		try {
			f.createNewFile();
			System.out.println(f.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		
		int count = 0;
		
		//从A中读取数据
		String cdataPath = "E:/diandao_poiid.csv";
		BufferedReader creader = new BufferedReader(new InputStreamReader(
				new FileInputStream(cdataPath), "GBK"));
		String cdataStr;
		while ((cdataStr = creader.readLine()) != null) {
			a.add(cdataStr);
		}
		
		//判断A中数据能否在B中找到
		String bPath = "E:/chen.csv";
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					new FileInputStream(bPath), "GBK"));
			String bdataStr;
			while( (bdataStr = breader.readLine()) != null){
				b.add(bdataStr);
			}
			
			for(String aString : a){
				if(!b.contains(aString)){
					count++;
					writer.write(aString);
					writer.write("\n");
				}
			}
		writer.close();
		System.out.println("A中有而B中没有的共有：" + count);
	}
}
