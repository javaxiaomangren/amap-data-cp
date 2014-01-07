/**
 * 2013-6-27
 */
package com.amap.data.save.lashou.fieldMap;

import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

/**
 * tcode和tname处理
 */
public class GroupTcodeTnameMap extends FieldMap {
	public GroupTcodeTnameMap(TempletConfig templet) {
		type = "动态信息映射";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Object group_tnameObj = from.get("group_tname");
		String group_tname = null;
		String group_tcode = null;
		if (group_tnameObj == null || group_tnameObj.equals("")) {
			group_tcode = "06";
		} else {
			group_tname = group_tnameObj.toString();
			if (group_tname.equalsIgnoreCase("餐饮美食")) {
				group_tcode = "01";
			} else if (group_tname.equalsIgnoreCase("生活服务")) {
				group_tcode = "02";
			} else if (group_tname.equalsIgnoreCase("休闲娱乐")) {
				group_tcode = "03";
			} else if (group_tname.equalsIgnoreCase("酒店旅游")) {
				group_tcode = "04";
			} else {
				group_tcode = "06";
			}
		}

		if (group_tcode.equalsIgnoreCase("01")) {
			group_tname = "美食天下";
		} else if (group_tcode.equalsIgnoreCase("02")) {
			group_tname = "生活服务";
		} else if (group_tcode.equalsIgnoreCase("03")) {
			group_tname = "休闲娱乐";
		} else if (group_tcode.equalsIgnoreCase("04")) {
			group_tname = "酒店旅游";
		} else {
			group_tname = "其他团购";
		}

		to.put("group_tcode", group_tcode);
		to.put("group_tname", group_tname);
		return true;
	}
}