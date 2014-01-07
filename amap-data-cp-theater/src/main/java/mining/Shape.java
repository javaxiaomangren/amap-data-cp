/**
 * 2013-12-5
 */
package mining;

import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 边界形状处理
 */
@SuppressWarnings("unchecked")
public class Shape {
	private static final String url = "http://192.168.3.125:8080/saveDeepRti/SaveDeepRti?";
	private static String cp = "mining_shape";
	private static final Logger log = LoggerFactory.getLogger(Shape.class);
	
	private static Set<String> names = new HashSet<String>();
	@SuppressWarnings("rawtypes")
	private static Map namePoiids = new HashMap();
	static {
		names.add("王府井");
		names.add("清华大学");
		names.add("北京西站");
		names.add("北京站");
		names.add("颐和园");
		names.add("西单");
		names.add("北京大学");
		names.add("圆明园");
		
		namePoiids.put("王府井", "B000A8WS91");
		namePoiids.put("清华大学", "B000A7BD6C");
		namePoiids.put("北京西站", "B000A83M61");
		namePoiids.put("西客站", "B000A83M61");
		namePoiids.put("北京站", "B000A83C36");
		namePoiids.put("颐和园", "B000A7O1CU");
		namePoiids.put("西单", "B000A8WS9A");
		namePoiids.put("北京大学", "B000A816R6");
		namePoiids.put("圆明园", "B000A16E89");
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void main(String[] args) throws IOException{
		String dataPath = "E://mohe1.txt";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataPath), "utf-8"));
		String data;
		data = reader.readLine();
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split(",;");
			if(names.contains(fields[0]) || "西客站".equals(fields[0])){
				LinkedHashMap temp = new LinkedHashMap();
				//获取并处理坐标信息
//				String coordinate = fields[5];
//				coordinate = coordinate.replace("\"[{\"\"x\"\":", "");
//				coordinate = coordinate.replace("\"\"y\"\":", "");
//				coordinate = coordinate.replace("},{\"\"x\"\":", ";");
//				coordinate = coordinate.replace("}]\"", "");
				temp.put("shape", fields[1]);
				
				//组装deep深度信息字段
				Map shape = new HashMap();
				shape.put(cp, temp);
				
				//拼装调用SaveDeepRti接口需要的字段信息
				Map urlMap = new HashMap();
				urlMap.put("flag", "deep");
				urlMap.put("cp", cp);
				Object cpid = GetIdFromTable.getIdfromTable(cp, namePoiids.get(fields[0]));
				if(cpid == null || cpid.equals("null")){
					log.info("获取cpid错误！！！当前数据没有入中间表");
					continue;
				}
				urlMap.put("cpid", cpid + "");
				urlMap.put("poiid", namePoiids.get(fields[0]));
				urlMap.put("deep", JSON.serialize(shape));
				
				String result = "";
				try {
					result = HttpclientUtil.post(url, urlMap, "UTF-8");
				} catch (Exception e) {
					log.info(e + "");
				}
				
				if(!"success".equals(result)){
					log.info("当前数据入中间表出错，当期数据是：" + fields[0]);
				}
			}
		}
	}
}
