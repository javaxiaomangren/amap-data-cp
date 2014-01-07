package com.amap.data.base.fieldfilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldFilter;
import com.amap.data.base.TempletConfig;

public class ValueListFilter extends FieldFilter {
	private List<String> filterList = null;
	private List<String> fieldList = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ValueListFilter(TempletConfig templet) {
		type= "字段值过滤";
		errValue = new HashMap();
		fieldList = templet.getList("value_filter_field");
		filterList = templet.getList("value_filter_content");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldfilter(Map from) {
		for (String field : fieldList) {
			String value = (String) from.get(field);
			int index = fieldList.indexOf(field);
			String s[] = filterList.get(index).split("\\|");
			for (String s1 : s) {
				if (s1.equals(value)) {
					errMessage=field;
					errValue.put(field,value);
					return false;
				}
			}
		}
		return true;
	}

}
