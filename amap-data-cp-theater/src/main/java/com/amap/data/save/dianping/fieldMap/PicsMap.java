/**
 * 2013-5-27
 */
package com.amap.data.save.dianping.fieldMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class PicsMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(PicsMap.class);
	public PicsMap(TempletConfig templet) {
		type = "点评深度信息图片映射，重复图片进行过滤";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");
		
		Set<LinkedHashMap> picSet = new HashSet<LinkedHashMap>();
		List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
		if(deep != null && deep.get("pic_info") != null){
			try{
				List<Map> pics = (List<Map>) deep.get("pic_info");
				for(Map m : pics){
					LinkedHashMap temp = new LinkedHashMap();
					temp.put("iscover", m.get("iscover") == null || m.get("iscover").equals("") ? null : m.get("iscover"));
					temp.put("src_type", m.get("src_type"));
					temp.put("title", m.get("title") == null || m.get("title").equals("") ? null : m.get("title"));
					temp.put("url", m.get("url"));
					if(picSet == null || !picSet.contains(temp)){
						picSet.add(temp);
						temps.add(temp);
					}
				}
			}catch (Exception e) {
				log.info("点评图片信息映射错误！！！");
			}
		}
		 to.put("pic_info", temps == null || temps.size() == 0 ? null : temps);
		return true;
	}
}