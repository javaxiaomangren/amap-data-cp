/**
 * 2013-5-27
 */
package com.amap.data.save.tuniu.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class PicsMap extends FieldMap {
	public PicsMap(TempletConfig templet) {
		type = "深度信息图片映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");

		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		if (deep != null && deep.get("pic_info") != null) {
			try {
				List<Map> pics = (List<Map>) deep.get("pic_info");
				for (Map pic : pics) {
					if (pic.get("url") == null || pic.get("url").equals("")) {
						continue;
					}
					LinkedHashMap temp = new LinkedHashMap();
					if (pic.get("default") != null && !"".equals(pic.get("default"))
							&& pic.get("default").toString().equals("1")) {
						temp.put("iscover", "1");
					} else {
						temp.put("iscover", "0");
					}
					temp.put("src_type", pic.get("src_type"));
					temp.put(
							"title",
							pic.get("title") == null
									|| pic.get("title").equals("") ? null : pic
									.get("title"));
					temp.put("url", pic.get("url"));
					temps.add(temp);
				}
			} catch (Exception e) {
			}
		}
		to.put("pic_info", temps == null || temps.size() == 0 ? null : temps);

		return true;
	}
}