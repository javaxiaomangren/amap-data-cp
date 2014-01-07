/**
 * 2013-5-27
 */
package com.amap.data.save.cinemamerge.fieldmap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class UrlMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(UrlMap.class);
	private List<String> urls;

	@SuppressWarnings("unchecked")
	public UrlMap(TempletConfig templet) {
		type = "深度url映射";
		urls = templet.getList("url_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		for(String url : urls){
			List<LinkedHashMap> temps = new ArrayList<LinkedHashMap>();
			try {
				List<Map> infourls = (List<Map>) from.get(url);
				for (Map m : infourls) {
					LinkedHashMap temp = new LinkedHashMap();
					for(Object key : m.keySet()){
						temp.put(key, m.get(key) == null || m.get(key).equals("") || m.get(key).equals("null") ? null : m.get(key));
					}
					temps.add(temp);
				}
			} catch (Exception e) {
				log.info(url + "映射错误");
				return false;
			}
			to.put(url, temps.size() == 0 ? null : temps);
		}
		return true;
	}
}