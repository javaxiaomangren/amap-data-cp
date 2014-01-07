/**
 * 2013-7-23
 */
package com.amap.data.save.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.base.http.HttpclientUtil;

/**
 * 根据输入的poiid，从数据网址接口获取测试数据
 */
public class GetTestDatas {
	//正式為false，測試為true
	private static boolean flag = true;
	//测试网开发环境：http://cmsdata.dev.myamap.com/amcddbl/v2/poi/nocache/
	//测试网测试环境：http://cmsdata.test.myamap.com/amcddbl2/v2/poi/nocache/
	private static String url = "http://cmsdata.dev.myamap.com/amcddbl/v2/poi/nocache/";
	
	private static void init(){
		if(!flag){
			url = "http://dbl.amap.com/amcddbl/v2/poi/nocache/";
		}
	}
	public static void main(String[] args) throws IOException{
		init();
		// 输出信息
		String path = "E:/hd.txt";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
				
//		String[] poiids = getPoiid().split(",");
		String[] poiids = getPoiidSql().split(",");
		String getUrl = "";
		int count = 0;
		System.out.println("共有poiid个数为：" + poiids.length);
		for(int i = 0; i < poiids.length; i++){
			String poiid = poiids[i];
			getUrl = url + poiid + "/all";
			String result = HttpclientUtil.get(getUrl);
			String json = JsonTool.formatJson(result, "  ");
			writer.write("/* " + i + " */");
			writer.write("\n");
			writer.write(json);
			writer.write("\n");
			writer.write("\n");
//			if(result.contains("indoormap_diandao_id")){
//				writer.write(poiid + "," + "点道&室内");
//			} else {
//				writer.write(poiid + "," + "室内");
//			}
//			writer.write("\n");
			if(i % 100 == 0){
				System.out.println("已经处理的个数为：" + i);
			}
		}
		System.out.println(count);
		writer.close();
	}
	
	//get poiid
	private static String getPoiid() throws IOException{
		String fullDataPath = "E://poiid.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullDataPath)));
		String data;
		
		String result = "";
		while ((data = reader.readLine()) != null) {
			String poiid = data;
			
			if(result == ""){
				result += poiid;
			}else{
				result += "," + poiid;
			}
		}
		System.out.println(result);
		System.out.println("\"" + result.replace(",", "\",\"") + "\"");
		return result;
	}
	
	//直接从sql中读取数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String getPoiidSql(){
		String sql = "SELECT DISTINCT poiid FROM poi_deep WHERE cp = 'hd' AND test_update_flag = 0 LIMIT 20";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> poiidDatas = ddr.readList();
		String result = "";
		for(Map poiidData : poiidDatas){
			Object poiid = poiidData.get("poiid");
			if(result.equals("")){
				result = poiid + "";
			} else {
				result += "," + poiid;
			}
		}
		System.out.println(result);
		return result;
	}
}
