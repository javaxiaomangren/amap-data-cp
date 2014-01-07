/**
 * 2013-12-17
 */
package com.amap.data.save.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;

public class GetSndtData {
	private static String fengefu = "\",\"";
	private static String url = "http://dbl.amap.com/amcddbl/v2/poi/nocache/";

	private final static Logger log = LoggerFactory.getLogger(GetSndtData.class);
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws IOException {
		// 输出信息
		String path = "E:/sndt_data.csv";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		
		//获取已入线上库的sndt的poiid
		String sql = "SELECT DISTINCT poiid FROM poi_deep WHERE cp = 'sndt' AND update_flag = 0 and deep not like '%\"status\": -1%'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> sndtDatas = ddr.readAll();
		log.info("共有poiid的个数为：" + sndtDatas.size());
		int count = 0;
		for(Map sndtData : sndtDatas){
			Object poiid = sndtData.get("poiid");
			String getUrl = url + poiid + "/all";
			String result = HttpclientUtil.get(getUrl);
			if(result.contains("EmptypoiId") || result.contains("OfflinepoiId") || !result.contains("idDictionaries")){
				continue;
			}
			
			Map resultMap = (Map) JSON.parse(result);
			Object poiinfo = resultMap.get("poiinfo");
			Map poiinfoMap = (Map) JSON.parse(poiinfo.toString());
			Object baseObj = poiinfoMap.get("base");
			Map base = (Map) JSON.parse(baseObj.toString());
			
			Object idObj = poiinfoMap.get("idDictionaries");
			Map iddictionaries = (Map) JSON.parse(idObj.toString());
			
			writer.write("\"" + poiid + fengefu + iddictionaries.get("sndt_id") + fengefu + base.get("name") + fengefu
					+ base.get("address") + fengefu + base.get("telephone") + fengefu + base.get("code") + fengefu + base.get("x") + fengefu + base.get("y") + fengefu);
			if(iddictionaries.containsKey("indoormap_diandao_id")){
				writer.write("室内&点道\"");
			} else {
				writer.write("室内\"");
			}
			writer.write("\n");
			writer.flush();
			count++;
			if(count % 100 == 0){
				log.info("已经处理的个数为：" + count);
			}
		}
		writer.close();
	}
}
