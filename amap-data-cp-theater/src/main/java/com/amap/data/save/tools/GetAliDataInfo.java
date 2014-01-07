/**
 * 2013-10-14
 */
package com.amap.data.save.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

/**
 * 导出已经上线的阿里数据
 */
public class GetAliDataInfo {
	
	private static String url = "http://dbl.amap.com/amcddbl/v2/poi/nocache/";
	private static boolean flag = false;//true:forzhiwei;flag:for guanghong

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		// 输出信息
		String path = "E:/aliinfo.csv";
		if(!flag){
			path = "E:/aliinfoheguanghong.csv";
		}
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		if(flag){
			writer.write("poiid,名称,FLAGSHIPSTORE_NAME,LOGO_URL,FLAGSHIPSTORE_WAPURL");
		} else {
			writer.write("poiid,名称,FLAGSHIPSTORE_NAME,LOGO_URL,FLAGSHIPSTORE_WAPURL");
		}
		writer.flush();
		writer.write("\n");

		String sql = "SELECT * from poiid";
		DBDataReader ddr = new DBDataReader(sql);
		List<Map> l = new ArrayList<Map>();
		l = ddr.readAll();
		System.out.println("共需要处理：" + l.size());
		
		int count = 0;
		for(Map m : l){
			count++;
			Object poiid = m.get("poiid");
			String getUrl = url + poiid + "/all";
			String result = HttpclientUtil.get(getUrl);
			Map poiinfo = (Map) JSON.parse(result);
			if (result.contains("spec")){
				Map res = (Map) JSON.parse(poiinfo.get("poiinfo").toString());
				Map ali = (Map) JSON.parse(res.get("spec").toString());
				Map spec = (Map) JSON.parse(ali.get("ali_activity_1111").toString());
				Map base = (Map) JSON.parse(res.get("base").toString());
				if(flag){
					writer.write(poiid + "," + base.get("name") + "," + spec.get("flagshipstore_name") + "," + spec.get("logo_url") + "," + spec.get("flagshipstore_wapurl") + ",");
				} else {
					writer.write(poiid + "," + spec.get("discount_num") + "," + spec.get("discount_amt"));
				}
				writer.write("\n");
				writer.flush();
			}
			
			if (count % 100 == 0){
				System.out.println(count);
			}
		}
		writer.close();
	}
}
