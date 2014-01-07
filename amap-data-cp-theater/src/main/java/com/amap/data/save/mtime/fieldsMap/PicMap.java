/**
 * 2013-8-9
 */
package com.amap.data.save.mtime.fieldsMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.cinema.fieldMap.PicsMap;

public class PicMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(PicsMap.class);

	public PicMap(TempletConfig templet) {
		type = "深度信息图片映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		try {
			List<Map> pics = (List<Map>) from.get("pic_info");
			for (Map m : pics) {
				LinkedHashMap temp = new LinkedHashMap();
				temp.put("iscover", m.get("iscover"));
				temp.put("src_type", m.get("src_type"));
				temp.put("title", m.get("title") == null
						|| m.get("title").equals("") ? null : m.get("title"));
				temp.put("url", m.get("url"));
				temps.add(temp);
			}
		} catch (Exception e) {
			log.info("图片信息映射错误！！！");
		}
		to.put("pic_info", temps.size() == 0 ? null : temps);
		return true;
	}
}