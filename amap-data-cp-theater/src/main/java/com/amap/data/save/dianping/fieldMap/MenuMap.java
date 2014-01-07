/**
 * 2013-10-23
 */
package com.amap.data.save.dianping.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;
import com.mongodb.util.JSON;

public class MenuMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(PicsMap.class);

	public MenuMap(TempletConfig templet) {
		type = "点评菜单信息映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");

		List<Map> temps = new ArrayList<Map>();
		if (deep != null && deep.get("menu_info") != null) {
			try {
				List<Object> menus = (List<Object>) deep.get("menu_info");
				for (Object menu : menus) {
					Map m = (Map) JSON.parse(menu.toString());
					m = SaveHelper.transferToSmall(m);
					m.put("pic_info", getPic(m));
					
					temps.add(m);
				}
			} catch (Exception e) {
				log.info("点评菜单信息映射错误！！！");
			}
		}
		to.put("menu_info", temps);
		return true;
	}

	// 菜单中的图片处理
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getPic(Map info) {
		if (info.get("pic_info") != null) {
			List<Map> temps = new ArrayList<Map>();
			try{
				List<Map> pics = (List<Map>) JSON.parse(info.get("pic_info").toString());
				for(Map pic : pics ){
					if (pic.get("URL") == null || pic.get("URL").equals("")) {
						return null;
					}
					LinkedHashMap temp = new LinkedHashMap();
					if (pic.get("ISCOVER") != null && pic.get("ISCOVER").equals("1")) {
						temp.put("iscover", "1");
					} else {
						temp.put("iscover", "0");
					}
					temp.put("src_type", pic.get("SRC_TYPE"));
					temp.put("title", pic.get("TITLE") == null
							|| pic.get("TITLE").equals("") ? null : pic.get("TITLE"));
					temp.put("url", pic.get("URL"));
					
					temps.add(temp);
				}
			}catch (Exception e) {
				//图片不是list，需要直接处理
				Map pic = (Map) JSON.parse(info.get("pic_info").toString());
				if (pic.get("URL") == null || pic.get("URL").equals("")) {
					return null;
				}
				LinkedHashMap temp = new LinkedHashMap();
				if (pic.get("ISCOVER") != null && pic.get("ISCOVER").equals("1")) {
					temp.put("iscover", "1");
				} else {
					temp.put("iscover", "0");
				}
				temp.put("src_type", pic.get("SRC_TYPE"));
				temp.put("title", pic.get("TITLE") == null
						|| pic.get("TITLE").equals("") ? null : pic.get("TITLE"));
				temp.put("url", pic.get("URL"));
				
				temps.add(temp);
			}
			
			return temps;
		}
		return null;
	}
}
