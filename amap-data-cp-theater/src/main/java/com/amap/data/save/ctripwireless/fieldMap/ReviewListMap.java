/**
 * 2013-5-27
 */
package com.amap.data.save.ctripwireless.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class ReviewListMap extends FieldMap {
	private List<String> review_cols;
	@SuppressWarnings("unchecked")
	public ReviewListMap(TempletConfig templet) {
		type = "动态信息评论映射";
		review_cols = templet.getList("review_cols");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<Map> review_list = (List<Map>) from.get("review_list");
		List<LinkedHashMap> review_lists = new ArrayList<LinkedHashMap>();
		for(Map m : review_list){
			LinkedHashMap review = new LinkedHashMap();
			for(String s : review_cols){
				review.put(s, m.get(s) == null || m.get(s).equals("") ? null : m.get(s));
			}
			review_lists.add(review);
		}
		 to.put("review_list", review_lists);
		return true;
	}
}
