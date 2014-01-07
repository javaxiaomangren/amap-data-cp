/**
 * 2013-5-27
 */
package com.amap.data.save.golf.fieldMap;

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
		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		if(from.get("pic_info") != null){
			List<Map> pics = (List<Map>) from.get("pic_info");
			for(Map pic : pics){
				if(pic.get("URL") == null || pic.get("URL").equals("")){
					continue;
				}
				LinkedHashMap temp = new LinkedHashMap();
				if (pic.get("DEFAULT") != null && pic.get("DEFAULT").equals("1")){
					temp.put("iscover", "1");
				} else {
					temp.put("iscover", "0");
				}
				temp.put("src_type", pic.get("SRC_TYPE"));
				temp.put("title", pic.get("TITLE") == null || pic.get("TITLE").equals("") ? null : pic.get("TITLE") );
				temp.put("url", pic.get("URL"));
				temps.add(temp);
			}
		}
		 to.put("pic_info", temps);
		return true;
	}
}