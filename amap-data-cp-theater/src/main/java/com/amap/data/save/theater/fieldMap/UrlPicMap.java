/**
 * 2013-7-8
 */
package com.amap.data.save.theater.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class UrlPicMap extends FieldMap {
	private String urlStr = "http://wap.damai.cn/project.aspx?id=";
	private String posturlStr = "http://pimg.damai.cn/perform/project/";
	public UrlPicMap(TempletConfig templet) {
		type = "动态信息url映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		String url = null;
		String post_url = null;
			Object rtiid = from.get("rtiid");
			if(rtiid != null && !rtiid.equals("")){
				url = urlStr + rtiid;
				
				//http://pimg.damai.cn/perform/project/项目ID除以100/项目ID_n.jpg
				int rtiidInt = Integer.parseInt(rtiid.toString());
				post_url = posturlStr + rtiidInt / 100 + "/" + rtiidInt + "_n.jpg";
			}
		to.put("activity_wapurl", url);
		to.put("coverimg", post_url);
		return true;
	}
}