/**
 * 2013-9-10
 */
package com.amap.data.save.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.mongodb.util.JSON;

public class ReadAllContent {
	//用于存储所有的字段值
	private static Set<String> keys = new LinkedHashSet<String>();
	private static OutputStreamWriter writer;
	
	static{
		File f0 = new File("e:/result.csv");
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writer = new OutputStreamWriter(new FileOutputStream(f0), "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException{
		//读取文本中的所有内容
		String json = getTxtContent();
		
		//处理json成指定的单一的map格式
		Map m = jsonSplit(json);
		
		//写结果
		writeResult(m);
	}
	
	/**
	 * 一次性读取文本中的全部内容，不按行读取
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static String getTxtContent() throws UnsupportedEncodingException{
		File file = new File("E:\\try.txt");
		Long fileLengthLong = file.length();
		byte[] fileContent = new byte[fileLengthLong.intValue()];
		try {
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(fileContent);
			inputStream.close();
		} catch (Exception e) {
		}
		return new String(fileContent, "gbk");
	}
	/**
	 * 写结果
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("rawtypes")
	private static void writeResult(Map m) throws IOException{
		//先写行标题
		for(String key : keys){
			writer.write(key);
			writer.write(",");
			writer.flush();
		}
		writer.write("\n");
		
		for(String key : keys){
			if("null".equalsIgnoreCase(m.get(key) + "")){
				writer.write("");
			} else {
				writer.write(m.get(key) + "");
			}
			writer.write(",");
			writer.flush();
		}
	}
	/**
	 * 判断当前传入的json是否可以转成单个的map，如果是返回处理后的map
	 */
	@SuppressWarnings("rawtypes")
	private static Map jsonSplit(String json){
		Map result = new HashMap();
		try{
			Map m = (Map) JSON.parse(json);
			for(Object key : m.keySet()){
				setKeys(key);
				result = jsonDigui(result, m.get(key), key);
			}
		}catch (Exception e) {
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map jsonDigui(Map m, Object value, Object key){
		if(value == null || value.equals("") || value.equals("null")){
			m.put(key, value);
		} else {
			m.put(key, value);
		}
		return m;
	}
	/**
	 * 判断当前值是否是个map，可拆分形式；是的话，返回true
	 */
	private static boolean assertMap(Object value){
		if(value != null && !value.equals("") && !value.equals("null")){
			
		}
		return false;
	}
	/**
	 * 判断keys中是否包含当前的string，如果没有包含，则存入；否则什么都不做
	 */
	private static void setKeys(Object key){
		if(keys == null || !keys.contains(key)){
			keys.add(key.toString());
		}
	}
	
}
