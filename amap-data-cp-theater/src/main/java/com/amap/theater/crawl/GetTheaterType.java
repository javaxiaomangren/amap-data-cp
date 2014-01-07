/**
 * 2013-9-2
 */
package com.amap.theater.crawl;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过分类接口，获取大麦网对应的分类
 */
public class GetTheaterType {
//	private static String urlString = "http://10.2.134.101:8079/acs/getMiddleType";
	//10.2.167.92
	private static String urlString = "http://10.2.167.92:8080/acs/getMiddleType";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		// 输出信息:用于保存大类正确的信息
		String path = "E:/rightType.txt";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		
		// 输出信息:用于保存大类错误的信息
		String path1 = "E:/wrongType.txt";
		File f1 = new File(path1);
		try {
			f1.createNewFile();
			System.out.println(f1.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer1 = new OutputStreamWriter(
				new FileOutputStream(f1), "gbk");
		
		
		// get data from sql
		String sql = "select distinct name from theatre_damai";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> datas = ddr.readAll();

		for (Map data : datas) {
			Map n = new HashMap();
			n.put("str", data.get("name"));
			String result = HttpclientUtil.get(urlString, n, "GBK");
			List<Map> resultMap = (List<Map>) JSON.parse(result);
			if(resultMap != null && resultMap.size() != 0){
				if(assertRight(getType(resultMap))){
					writer.write(data.get("name") + "," + getType(resultMap));
					writer.write("\n");
					writer.flush();
				}else{
					writer1.write(data.get("name") + "," + getType(resultMap));
					writer1.write("\n");
					writer1.flush();
				}
			}else{
				writer1.write(data.get("name") + "," + "");
				writer1.write("\n");
				writer1.flush();
			}
		}
		writer.close();
	}
	
	/**
	 * 判断是否包含指定大类，如果不包含的话，就返回第一个，否则返回指定的大类
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String getType(List<Map> resultMap){
		//赋初值
		List<Map> types = (List<Map>) resultMap.get(0).get("types");
		
		int i = 0;
		while(i <= resultMap.size()){
			if(types.size() == 0){
				i ++;
				if(i < resultMap.size()){
					types = (List<Map>) resultMap.get(i).get("types");
				}else{
					i = resultMap.size() + 1;
				}
			}else{
				types = (List<Map>) resultMap.get(i).get("types");
				i = resultMap.size() + 1;
			}
		}
		if(types.size() == 0){
			return null;
		}
		String type = null;
		try{
			type = types.get(0).get("mtype") + "";
			for(Map m : resultMap){
				List<Map> temp = (List<Map>) m.get("types");
				if(temp.size() == 0){
					continue;
				}
				String t = temp.get(0).get("mtype") + "";
				if(t.startsWith("08") || t.startsWith("11") || t.startsWith("14")){
					return t;
				}
			}
		}catch (Exception e) {
			System.out.println();
		}
		return type;
	}
	
	//判断type是否在指定的大类中
	private static boolean assertRight(String t){
		if(t != null && (t.startsWith("08") || t.startsWith("11") || t.startsWith("14"))){
			return true;
		}
		return false;
	}
}
