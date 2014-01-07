package com.amap.data.save.tools;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.mongodb.util.JSON;


/**
 * 2013-12-4
 */

/**
 * 魔盒计划中的坐标处理：处理成x1,y1 ; x2,y2 ; x3,y3
 */
@SuppressWarnings("unchecked")
public class Dealcoordiname {
	private static Set<String> names = new HashSet<String>();
	@SuppressWarnings("rawtypes")
	private static Map children = new HashMap();
	static{
		names.add("王府井");
		names.add("清华大学");
		names.add("北京西站");
		names.add("北京站");
		names.add("颐和园");
		names.add("西单");
		names.add("北京大学");
		names.add("圆明园");
		
		//配置父子关系
		children.put("清华大学", "B000A80Z29;B000A87L0Q;B000A7GRM1;B000A7IF8E;B000A830DB;B000A84BAO;B000A492D8");
		children.put("北京西站","B000A9QAFJ;B000A9R4Z3;B000A7IW9J;B000A81GCZ");
		children.put("北京站","B000A81GQP");
		children.put("颐和园","B000A81K3J;B000A85V4D;B000A7R1S2;B000A0CDA0;B000A82IHV;B000A9V81E");
		children.put("北京大学","B000A9JU6Z;B000A7VMCM;B000A805DO;B000A3B94F;B000A87IZ5;B000A843TI;B000A7XYYX");
		children.put("圆明园","B000A7VL63;B000A85A71;B000A58162;B000A85ULA");
		children.put("西客站","B000A9QAFJ;B000A9R4Z3;B000A7IW9J;B000A81GCZ");
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void main(String[] args) throws IOException{
		String dataPath = "E://mohe.txt";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataPath), "gbk"));
		String data;
		data = reader.readLine();
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split("	");
			if(names.contains(fields[1]) || "西客站".equals(fields[1])){
				System.out.println(fields[1]);
				LinkedHashMap temp = new LinkedHashMap();
				String coordinate = fields[5];
				coordinate = coordinate.replace("\"[{\"\"x\"\":", "");
				coordinate = coordinate.replace("\"\"y\"\":", "");
				coordinate = coordinate.replace("},{\"\"x\"\":", ";");
				coordinate = coordinate.replace("}]\"", "");
				temp.put("shape", coordinate);
				if(children.get(fields[1]) != null){
					temp.put("children", children.get(fields[1]));
				}
				
				Map mining = new HashMap();
				mining.put("mining", temp);
				String json = JSON.serialize(mining);
				System.out.println(json);
			}
		}
	}
}
