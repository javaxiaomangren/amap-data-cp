/**
 * 2013-6-18
 */
package com.amap.data.save.ctripwireless.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

/**
 * 拼装url
 */
public class InfoWeburlMap extends FieldMap {
	
	//http://hotels.ctrip.com/hotel/347314.html
	private String url = "http://hotels.ctrip.com/hotel/";
	public InfoWeburlMap(TempletConfig templet) {
		type = "info_weburl映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		try{
			String info_weburl;
			Object id = from.get("id");
			info_weburl = url + id + ".html";
			to.put("info_weburl", info_weburl);
		}catch (Exception e) {
			return false;
		}
		
		return true;
	}
}