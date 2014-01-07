package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.amap.base.http.HttpclientUtil;

/**
 * 重新触发数据
 */
public class TestMerge2Interface {
	//标记是测试环境还是正式：flag为true代表测试
	private static boolean flag = true;
	private static String urlString = "http://192.168.3.215:80/amap_save/merge";
	private static void init(){
		if(flag){
			urlString = "http://10.2.134.23:8080/amap_save/merge";
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		init();
		//从文件中读取数据：poiid和融合状态
		String fullDataPath = "E://poiid.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullDataPath)));
		String data;
		int num = 0;
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split(",");
			String poiid = fields[0];
			String status = fields[1];
			Map m = new HashMap();
			m.put("poiid", poiid);
			m.put("status", status);
			String result = HttpclientUtil.post(urlString, m);
			if(result == null || !result.contains("success")){
				System.out.println(result);
				System.out.println(poiid + "," + status);
			}
			num++;
			if(num % 1000 == 0){
				System.out.println(num);
			}
		}
	}
}
