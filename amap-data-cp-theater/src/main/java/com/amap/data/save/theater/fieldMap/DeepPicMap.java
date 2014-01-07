/**
 * @caoxuena
 * 2013-4-3
 *DeepPicMap.java
 */
package com.amap.data.save.theater.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;

public class DeepPicMap extends FieldMap {
	private List<String> pics;
	@SuppressWarnings("unchecked")
	public DeepPicMap(TempletConfig templet) {
		type = "深度信息图片映射";
		pics = templet.getList("deepinfo_pics");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> temp = new ArrayList<LinkedHashMap>();
		from = SaveHelper.transferToSmall(from);
		for (String pic : pics) {
			if(from.get(pic) == null || from.get(pic).equals("")){
				continue;
			}
			LinkedHashMap picTemp = new LinkedHashMap();
			picTemp.put("iscover", "0");
			picTemp.put("src_type", from.get("cp"));
			picTemp.put("title", "");
			picTemp.put("url", from.get(pic));
			temp.add(picTemp);
		}
		 to.put("pic_info", temp);
		return true;
	}
}
