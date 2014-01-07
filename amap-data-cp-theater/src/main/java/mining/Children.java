/**
 * 2013-12-5
 */
package mining;

import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 父子关系处理
 */
@SuppressWarnings("unchecked")
public class Children {
	private static final String url = "http://192.168.3.125:8080/saveDeepRti/SaveDeepRti?";
	private static String cp = "mining_children";
	private static final Logger log = LoggerFactory.getLogger(Children.class);

	@SuppressWarnings("rawtypes")
	private static Map childrens = new HashMap();
	static {
		childrens.put("B000A7BD6C", "B000A80Z29;B000A96FLJ;B000A87L0Q;B000A7GRM1;B000A7IF8E;B000A830DB;B000A84BAO;B000A492D8;B000AA45WC;B000A9PILN;B000A96FPX;B000A7O0UO");
		childrens.put("B000A83M61", "B000A81GCZ;B000A9QAFJ;B000A9R4Z3;B000A7IW9J;B000A87TV9");
		childrens.put("B000A83C36", "B000A70CEC;B000A81GQP");
		childrens.put("B000A7O1CU", "B000A81K3J;B000A9PI4E;B000A85V4D;B000A7R1S2;B000A9V81E;B000A0CDA0;B000A82IHV");
		childrens.put("B000A816R6", "B000A8U0V0;B000A9JU6Z;B000A7VMCM;B000A805DO;B000A3B94F;B000A87IZ5;B000A843TI;B000A7XYYX;B000A8ZIQX;B000A84C4S;B000A192CC");
		childrens.put("B000A16E89", "B000A7VL63;B000A85A71;B000A58162;B000A85ULA;B000A85F0W;B000AA45VI");
	}

	@SuppressWarnings({ "rawtypes" })
	public static void main(String[] args) throws IOException {
		for (Object key : childrens.keySet()) {
			LinkedHashMap temp = new LinkedHashMap();
			// 获取父子信息
			temp.put("children", childrens.get(key));

			// 组装deep深度信息字段
			Map shape = new HashMap();
			shape.put(cp, temp);

			// 拼装调用SaveDeepRti接口需要的字段信息
			Map urlMap = new HashMap();
			urlMap.put("flag", "deep");
			urlMap.put("cp", cp);
			Object cpid = GetIdFromTable.getIdfromTable(cp, key);
			if (cpid == null || cpid.equals("null")) {
				log.info("获取cpid错误！！！当前数据没有入中间表");
				continue;
			}
			urlMap.put("cpid", cpid + "");
			urlMap.put("poiid", key);
			urlMap.put("deep", JSON.serialize(shape));

			String result = "";
			try {
				result = HttpclientUtil.post(url, urlMap, "UTF-8");
			} catch (Exception e) {
				log.info(e + "");
			}

			if (!"success".equals(result)) {
				log.info("当前数据入中间表出错，当期数据是：" + key);
			}

		}
	}
}
