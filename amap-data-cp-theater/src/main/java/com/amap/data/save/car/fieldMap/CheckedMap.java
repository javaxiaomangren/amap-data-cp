/**
 * 2013-5-16
 */
package com.amap.data.save.car.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class CheckedMap extends FieldMap {
	private String checkedMap;
	public CheckedMap(TempletConfig templet) {
		type = "Checked映射";
		checkedMap = templet.getString("checked_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map base = (Map) from.get(checkedMap);
		Object checkedObj = base.get("checked");
		String checked = null;
		if(checkedObj != null){
			checked = checkedObj.toString();
		}
		 to.put("checked", checked);
		return true;
	}
}

