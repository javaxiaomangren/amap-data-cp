/**
 * 2013-9-4
 */
package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class StringMap extends FieldMap {
	private List<String> stringMap = new ArrayList<String>();
	@SuppressWarnings("unchecked")
	public StringMap(TempletConfig templet) {
		type= "把来源是Object的映射成对应的String，并且为空的保持不变";
		
		stringMap = templet.getList("string_trans_map");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		//遍历需要进行字符串转换的各字段
		for (int i = 0; i < stringMap.size(); i++) {
			String ziduanName = stringMap.get(i);
			Object o = from.get(ziduanName);

			String ziduanInfo = null;

			if (o != null) {
				ziduanInfo = o.toString();
				if (ziduanInfo.equalsIgnoreCase("")
						|| ziduanInfo.equalsIgnoreCase("null")) {
					ziduanInfo = null;
				}
			}
			to.put(ziduanName, ziduanInfo);
		}
		return true;
	}

}