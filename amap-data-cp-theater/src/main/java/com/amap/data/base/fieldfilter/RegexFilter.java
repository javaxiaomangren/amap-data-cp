package com.amap.data.base.fieldfilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.data.base.FieldFilter;
import com.amap.data.base.TempletConfig;

public class RegexFilter extends FieldFilter {
	private List<String> fl;
	private List<String> ziduanName = new ArrayList<String>();
	private List<String> ziduanFilter = new ArrayList<String>();
	private int dealNum;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RegexFilter(TempletConfig templet) {
		type = "特殊字段过滤";
		Map m = new HashMap();
		errValue = m;

		// regex_map=address,des,name
		fl = templet.getList("regex_filter");
		dealNum = fl.size();
		for (int i = 0; i < dealNum; i++) {
			ziduanName.add(fl.get(i));
			String tempName = fl.get(i) + "_regex_filter";
			List<String> temp = templet.getList(tempName);
			String filterZiduan = "";
			for(int k = 0; k < temp.size(); k++){
				if(k == 0){
					filterZiduan += temp.get(k);
				}else{
					filterZiduan += "," + temp.get(k);
				}
			}
			ziduanFilter.add(filterZiduan);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldfilter(Map from) {
		for (int i = 0; i < dealNum; i++) {
			// 获取要特殊处理的字段
			String ziduanInfo = (String) from.get(ziduanName.get(i));

			// 获取定义的正则匹配
			String[] fields = ziduanFilter.get(i).split(",");
			for (int j = 0; j < fields.length; j++) {
				String str = fields[j];

				String regex = "([\u4e00-\u9fa5]{0,}\\d{0,}" + str
						+ ")";

				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(ziduanInfo);
				while (m.find()) {
					errMessage = ziduanName.get(i) + "字段过滤";
					errValue.put(ziduanName.get(i), ziduanInfo);
					return false;
				}
			}
		}

		return true;
	}
}