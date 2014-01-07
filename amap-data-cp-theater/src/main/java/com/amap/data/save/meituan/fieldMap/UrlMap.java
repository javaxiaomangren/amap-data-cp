/**
 * 2013-7-16
 */
package com.amap.data.save.meituan.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class UrlMap extends FieldMap {
	private String url = "http://r.union.meituan.com/url/visit/?a=1&key=KNJTcmtRhDHAex0CM1vlLyWfBaip7XkU&url=";
	public UrlMap(TempletConfig templet) {
		type = "动态信息info_wapurl和info_weburl映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		String info_wapurl = null;
		String info_weburl = null;
		Object wapurlObj = from.get("info_wapurl");
		if(wapurlObj != null && !wapurlObj.equals("") && !wapurlObj.equals("null")){
			info_wapurl = url + wapurlObj;
		}
		
		Object weburlObj = from.get("info_weburl");
		if(weburlObj != null && !weburlObj.equals("") && !weburlObj.equals("null")){
			info_weburl = url + weburlObj;
		}
		
		to.put("info_wapurl", info_wapurl);
		to.put("info_weburl", info_weburl);
		return true;
	}
}
