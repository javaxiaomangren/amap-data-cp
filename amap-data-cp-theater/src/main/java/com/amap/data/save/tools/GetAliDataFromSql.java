/**
 * 2013-10-14
 */
package com.amap.data.save.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amap.base.data.DBDataReader;
import com.mongodb.util.JSON;

public class GetAliDataFromSql {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException{
		String path = "E:/aliSql.csv";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		writer.write("SHOPID,MK_POIID,DISCOUNT_NUM,DISCOUNT_AMT,DISCOUNT_NUM_BOUND,DISCOUNT_AMT_BOUND,SPECPRODUCT_NUM,DISCOUNT_LOWEST,INTRO,BRAND_ID,BRAND_NAME,FLAGSHIPSTORE_NAME,FLAGSHIPSTORE_WEBURL,FLAGSHIPSTORE_WAPURL,LOGO_URL,POPULAR_VALUE");
		writer.write("\n");
		
		Set<Object> poiids = new HashSet<Object>();
		
		String sql = "SELECT * FROM poi_deep WHERE cp = 'ali_activity_1111' AND updatetime > '2013-9-28 18:32:46' and update_flag = 0";
		DBDataReader ddr = new DBDataReader(sql);
		List<Map> l = new ArrayList<Map>();
		l = ddr.readAll();
		
		for(Map m : l){
			Object poiid = m.get("poiid");
			
			if(poiids.contains(poiid)){
				continue;
			}
			poiids.add(poiid);
			
			Map deep = (Map) JSON.parse(m.get("deep").toString());
			Object SHOPID = deep.get("SHOPID");
			Object MK_POIID = deep.get("MK_POIID");
			Object DISCOUNT_NUM = deep.get("DISCOUNT_NUM");
			Object DISCOUNT_AMT = deep.get("DISCOUNT_AMT");
			Object DISCOUNT_NUM_BOUND = deep.get("DISCOUNT_NUM_BOUND");
			Object DISCOUNT_AMT_BOUND = deep.get("DISCOUNT_AMT_BOUND");
			
			Object INTRO = deep.get("INTRO");
			Object BRAND_ID = deep.get("BRAND_ID");
			Object BRAND_NAME = deep.get("BRAND_NAME");
			Object FLAGSHIPSTORE_NAME = deep.get("FLAGSHIPSTORE_NAME");
			Object FLAGSHIPSTORE_WEBURL = deep.get("FLAGSHIPSTORE_WEBURL");
			Object FLAGSHIPSTORE_WAPURL = deep.get("FLAGSHIPSTORE_WAPURL");
			Object LOGO_URL = deep.get("LOGO_URL");
			Object POPULAR_VALUE = deep.get("POPULAR_VALUE");
			
			Object SPECPRODUCT_NUM = deep.get("SPECPRODUCT_NUM");
			Object DISCOUNT_LOWEST = deep.get("DISCOUNT_LOWEST");
			
			 
			writer.write(SHOPID + "," + MK_POIID + "," + DISCOUNT_NUM + "," + DISCOUNT_AMT + "," + DISCOUNT_NUM_BOUND + "," + DISCOUNT_AMT_BOUND + "," + SPECPRODUCT_NUM + "," + DISCOUNT_LOWEST + "," + INTRO + "," + BRAND_ID + "," + BRAND_NAME + "," + FLAGSHIPSTORE_NAME + "," + FLAGSHIPSTORE_WEBURL + "," + FLAGSHIPSTORE_WAPURL + "," + LOGO_URL + "," + POPULAR_VALUE);
			writer.write("\n");
		}
		writer.close();
	}
}
