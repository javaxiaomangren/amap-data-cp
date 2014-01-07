package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class CrawlField extends FieldMap {
	private List<String> fieldList = new ArrayList<String>();

	@SuppressWarnings({ "unchecked" })
	public CrawlField(TempletConfig templet) {
		type= "名称映射";
		fieldList = templet.getList("crawl_field");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		for (String s : fieldList) {
			to.put(s, from.get(s));
		}
		return true;
	}
}
