package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.ConfigUtil;
import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class PicInfoMap extends FieldMap {

	private String picField;
	@SuppressWarnings("unused")
	private String prefix;
	private String srcType;

	public PicInfoMap(TempletConfig templet) {
		picField = templet.getString("pic_field_map");
		prefix = ConfigUtil.getString("pic_prefix");
		srcType = templet.getString("src_type");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		try {
			Object o = from.get(picField);
			
			if("".equals(o) || o == null){
				to.put(picField, null);
				return true;
			}
			
			List<Map> picList = (List<Map>) o;
			List<Map> toList = new ArrayList<Map>();

			for (Map m : picList) {
				Map tempMap = new LinkedHashMap();

				// 来源、标题
				tempMap.put("iscover", 0);
				tempMap.put("src_type", srcType);
				String title = ObjectUtil.toString(m.get("REMARKS"));
				title = title.replaceAll("^\\[", "").replaceAll("\\]$", "");
				tempMap.put("title", title);

				//如果有ID，fetch_type设置为1，有pic_id字段
				if (m.get("PIC_ID") != null
						&& ((List) m.get("PIC_ID")).size() != 0) { // 如果是云平台图片
					tempMap.put("fetch_type", "1");
					tempMap.put("pic_id", m.get("PIC_ID"));
					toList.add(tempMap);
				} else {
					//没有Id，fetch_type设置为0
					List l = (List) m.get("URL");
					if (l.size() != 0 && ((Map) l.get(0)).get("URL") != null) {
						tempMap.put("url", ((Map) l.get(0)).get("URL"));
						tempMap.put("fetch_type", "0");
						toList.add(tempMap);
					} else {
						continue;
					}
				}

			}

			to.put(picField, toList);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			to.put(picField, null);
			errValue = new HashMap();
			errValue.put(picField, from.get(picField));
			return false;
		}

	}

	@Override
	public String getType() {
		return "图片信息解析错误";
	}
}
