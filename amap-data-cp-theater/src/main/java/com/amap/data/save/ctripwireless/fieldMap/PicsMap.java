/**
 * 2013-5-27
 */
package com.amap.data.save.ctripwireless.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class PicsMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(PicsMap.class);
	private String pic;
	public PicsMap(TempletConfig templet) {
		type = "深度信息图片映射";
		pic = templet.getString("deepinfo_pics");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");
		
		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		if(deep != null && deep.get(pic) != null){
			try{
				List<Map> pics = (List<Map>) deep.get(pic);
				for(Map m : pics){
					LinkedHashMap temp = new LinkedHashMap();
					temp.put("iscover", "0");
					temp.put("src_type", m.get("src_type"));
					temp.put("title", m.get("title"));
					temp.put("url", m.get("url"));
					temps.add(temp);
				}
			}catch (Exception e) {
				log.info("携程图片信息映射错误！！！");
			}
		}
		 to.put("pic_info", temps);
		return true;
	}
}