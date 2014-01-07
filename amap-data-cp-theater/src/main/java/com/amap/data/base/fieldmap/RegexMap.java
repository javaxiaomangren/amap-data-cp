package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

//caoxuena：通用正则匹配替换
public class RegexMap extends FieldMap {
	private List<String> fm;
	private List<String> ziduanName = new ArrayList<String>();
	private List<String> ziduanMap = new ArrayList<String>();
	private int dealNum;

	@SuppressWarnings("unchecked")
	public RegexMap(TempletConfig templet) {
		type = "特殊字段映射";

		// regex_map=address,des,name
		fm = templet.getList("regex_map");
		dealNum = fm.size();
		for (int i = 0; i < dealNum; i++) {
			ziduanName.add(fm.get(i));
			String tempName = fm.get(i) + "_regex_map";
			List<String> temp = templet.getList(tempName);
			String mapZiduan = "";
			for (int k = 0; k < temp.size(); k++) {
				if (k == 0) {
					mapZiduan += temp.get(k);
				} else {
					mapZiduan += "," + temp.get(k);
				}
			}
			ziduanMap.add(mapZiduan);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		for (int i = 0; i < dealNum; i++) {
			// 获取要特殊处理的字段
			String ziduanInfo = (String) from.get(ziduanName.get(i));

			// 获取定义的正则匹配
			String[] fields = ziduanMap.get(i).split(",");
			for (int j = 0; j < fields.length; j++) {
				String oldStr = fields[j].split("-")[0];
				String newStr = "";
				if (fields[j].split("-").length == 2
						&& fields[j].split("-")[1] != null) {
					newStr = fields[j].split("-")[1];
				}

				ziduanInfo = ziduanInfo.replaceAll(oldStr, newStr);
			}

			to.put(ziduanName.get(i), ziduanInfo);
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		TempletConfig templet = new TempletConfig("theatre_damai_cp_deep_1");

		List<String> fm;
		List<String> ziduanName = new ArrayList<String>();
		List<String> ziduanMap = new ArrayList<String>();
		int dealNum;

		fm = templet.getList("regex_map");
		dealNum = fm.size();
		for (int i = 0; i < dealNum; i++) {
			ziduanName.add(fm.get(i));
			String tempName = fm.get(i) + "_regex_map";
			List<String> temp = templet.getList(tempName);
			String mapZiduan = "";
			for (int k = 0; k < temp.size(); k++) {
				if (k == 0) {
					mapZiduan += temp.get(k);
				} else {
					mapZiduan += "," + temp.get(k);
				}
			}
			ziduanMap.add(mapZiduan);
		}

		for (int i = 0; i < dealNum; i++) {
			// 获取要特殊处理的字段
			String ziduanInfo = "方恒.国 际  中心1【预付】{已售完}";

			// 获取定义的正则匹配
			String[] fields = ziduanMap.get(i).split(",");
			for (int j = 0; j < fields.length; j++) {
				String oldStr = fields[j].split("-")[0];
				String newStr = "";
				if (fields[j].split("-").length == 2
						&& fields[j].split("-")[1] != null) {
					newStr = fields[j].split("-")[1];
				}

				ziduanInfo = ziduanInfo.replaceAll(oldStr, newStr);
			}
			System.out.println(ziduanInfo);
		}
	}
}
