/**
 * 2013-5-27
 */
package com.amap.data.save.scenic.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class PicsMap extends FieldMap {
	public PicsMap(TempletConfig templet) {
		type = "动态信息图片映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		if(from.get("pic_info") != null){
			List<Map> pics = (List<Map>) from.get("pic_info");
			for(Map pic : pics){
				if(pic.get("url") == null || pic.get("url").equals("")){
					continue;
				}
				LinkedHashMap temp = new LinkedHashMap();
				temp.put("iscover", pic.get("iscover"));
				temp.put("src_type", pic.get("src_type"));
				temp.put("title", pic.get("title") == null || pic.get("title").equals("") ? null : pic.get("title") );
				temp.put("url", pic.get("url"));
				temps.add(temp);
			}
		}
		 to.put("pic_info", temps);
		return true;
	}
}