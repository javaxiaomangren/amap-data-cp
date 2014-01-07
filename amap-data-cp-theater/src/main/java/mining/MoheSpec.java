/**
 * 2013-12-25
 */
package mining;

import com.amap.base.http.HttpclientUtil;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 魔盒计划：部分深度需要修改，把对应字段放入spec中单独处理
 */
public class MoheSpec {
	private static final String url = "http://192.168.3.125:8080/saveDeepRti/SaveDeepRti?";
	private static String cp = "mohe_spec";
	private static final Logger log = LoggerFactory.getLogger(MoheSpec.class);
	@SuppressWarnings("rawtypes")
	private static Map poiidSpecInfos = new HashMap();

	// 初始化各poiid的信息
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void init() {
		// 天安门：B000A60DA1
		Map spec = new HashMap();
		spec.put("level", "4A");
		spec.put("price", "￥15");
		spec.put("opentime2", "4:30-17:00");
		spec.put("review", "毛主席;庄严;中轴线;中国的标志建筑;长安街;历史啊，历史;");
		spec.put("url", "http://store.is.autonavi.com/showpic/a4f3f36fe851674dc51d63842eb22141");
		poiidSpecInfos.put("B000A60DA1", spec);

		// 夫子庙：B00190ANHZ
		spec = new HashMap();
		spec.put("level", "4A");
		spec.put("price", "免费");
		spec.put("opentime2", "08:30-22:00");
		spec.put("review", "秦淮河;小家碧玉;秦淮小吃;热闹;元宵节花灯;静谧，迷人;到处都是人");
		spec.put("url", "http://store.is.autonavi.com/showpic/a480be2d45d974aa2d4bd3a3296cebec");
		poiidSpecInfos.put("B00190ANHZ", spec);

		// 颐和园
		spec = new HashMap();
		spec.put("level", "5A");
		spec.put("price", "￥30(旺季);￥20(淡季)");
		spec.put("opentime2", "06:30-20:00(旺季);07:00-19:00(淡季)");
		spec.put("review", "十七孔桥;皇家园林;佛香阁;风景好，人太多;适合拍照;人在画中游;");
		spec.put("url", "http://store.is.autonavi.com/showpic/af640b4827a95465c0428a931056e8a9");
		poiidSpecInfos.put("B000A7O1CU", spec);

		// 圆明园
		spec = new HashMap();
		spec.put("level", "5A");
		spec.put("price", "￥10");
		spec.put("opentime2",
				"07:00-19:30(1-3,11-12月)；07:00-20:30(4,9,10月); 07:00-21:00(5-8月)");
		spec.put("review", "历史的苍凉见证;十二生肖兽首;荷花月色;美，历史的伤;大水法;北京的伤疤;");
		spec.put("url", "http://store.is.autonavi.com/showpic/24fbca34f541d6cc24f24e3fe0e50608");
		poiidSpecInfos.put("B000A16E89", spec);

		// 清华大学
		spec = new HashMap();
		spec.put("tag_category", "公立全国重点大学");
		spec.put("tag_property", "综合");
		spec.put("tag_class", "111");
		spec.put("url", "http://store.is.autonavi.com/showpic/25de20f26ed366f02b1bcfad44bafe13");
		poiidSpecInfos.put("B000A7BD6C", spec);

		// 北京大学
		spec = new HashMap();
		spec.put("tag_category", "公立全国重点大学");
		spec.put("tag_property", "综合");
		spec.put("tag_class", "111");
		spec.put("url", "http://store.is.autonavi.com/showpic/4d8177cc897c5c22d476be2e98e55494");
		poiidSpecInfos.put("B000A816R6", spec);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		init();
		for (Object key : poiidSpecInfos.keySet()) {
			// 组装deep深度信息字段
			Map spec = new HashMap();
			spec.put(cp, poiidSpecInfos.get(key));

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
			urlMap.put("deep", JSON.serialize(spec));

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
