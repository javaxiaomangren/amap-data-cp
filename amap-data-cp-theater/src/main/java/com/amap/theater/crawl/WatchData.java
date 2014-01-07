/**
 * 2013-9-2
 */
package com.amap.theater.crawl;

import com.amap.data.save.SaveHelper;
import com.mongodb.util.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取下来的数据，要先走准入系统进行数据清洗，用清洗过的数据进行人工匹配确认； 该class类实现数据清洗
 */
public class WatchData {
	private static String urlString = "http://10.19.1.130:10087/CPAPlatform/TransformData";

	// 大麦网默认类别是140000
	private static String type = "140000";

	// 指定编码方式
	private static String charset = "GBK";
	
	private static HttpPost httppost;
	private static DefaultHttpClient httpclient = new DefaultHttpClient();
	
	public WatchData() {
		// 代理的设置
		HttpHost proxy = new HttpHost("10.19.1.130", 10087);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		// 目标地址
		httppost = new HttpPost(urlString);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getBase(Map data) throws IOException {
		data = SaveHelper.transferToSmall(data);
		Map m = new HashMap();
		m.put("poiid", null);
		m.put("cpid", data.get("id") + "");
		m.put("cpname", "theater");
		m.put("name", URLEncoder.encode(data.get("name") + "", charset));
		m.put("address", URLEncoder.encode(data.get("address") + "", charset));
		m.put("tel", data.get("tel"));
		m.put("type", type);
		m.put("cityname", URLEncoder.encode(data.get("cityname") + "", charset));
		m.put("districtcode", null);
		m.put("x", data.get("lng") + "");
		m.put("y", data.get("lat") + "");
		m.put("reserved", null);

		// 构造最简单的字符串数据
		StringEntity reqEntity = new StringEntity(JSON.serialize(m));
		// 设置类型
		reqEntity.setContentType("application/x-www-form-urlencoded");
		// 设置请求的数据
		httppost.setEntity(reqEntity);
		// 执行
		HttpResponse response = httpclient.execute(httppost);
		httpclient = new DefaultHttpClient();
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		// 显示结果
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent(), charset));
		String line = reader.readLine();
		if (line != null && line.contains("成功")) {
			Map lineMap = null;
			try {
				lineMap = (Map) JSON.parse(line);
			} catch (Exception e) {
				return null;
			}
			try{
				return (Map) JSON.parse(lineMap.get("base").toString());
			}catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
