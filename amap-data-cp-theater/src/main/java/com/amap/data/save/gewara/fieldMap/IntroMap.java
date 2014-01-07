/**
 * 2013-8-9
 */
package com.amap.data.save.gewara.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class IntroMap extends FieldMap {
	private String intro;

	public IntroMap(TempletConfig templet) {
		type = "深度intro映射";
		intro = templet.getString("intro_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Object introOj = from.get(intro);
		if(introOj != null && (introOj.equals("") || introOj.equals("["))){
			introOj = null;
		}
		to.put("intro", introOj);
		return true;
	}
}