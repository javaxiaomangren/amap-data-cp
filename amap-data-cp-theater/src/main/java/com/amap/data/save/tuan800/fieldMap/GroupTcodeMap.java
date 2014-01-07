/**
 * 2013-5-27
 */
package com.amap.data.save.tuan800.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class GroupTcodeMap extends FieldMap {

	public GroupTcodeMap(TempletConfig templet) {
		type = "group_tcode映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Object group_tcodeObj = from.get("group_tcode");
		String group_tcode = null;
		if(group_tcodeObj != null && !group_tcodeObj.equals("")){
			group_tcode = group_tcodeObj.toString();
			if(group_tcode.length() == 1){
				group_tcode = "0" + group_tcode;
			}
		}
		to.put("group_tcode", group_tcode);
		return true;
	}
}