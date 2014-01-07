package com.amap.data.base.fieldmap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

//caoxuena：函数功能为替换名称中的非法及无用字段
public class NameMap extends FieldMap {
	private String fm;

	public NameMap(TempletConfig templet) {
		type = "名字name_especial_map映射";

		fm = templet.getString("name_especial_map");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		if (fm != null) {
			String name = (String) from.get("name_chn");
			// 去空格
			name = name.replace(" ", "");

			// 定义正则匹配
			String[] regExs = new String[5];
			regExs[0] = "(\\<br\\>)";// 乱字符<br>
			// 去掉“【预付】”、“——”、““””、“（3星标准）”、“（预付：酒店＋景点）”）
			regExs[1] = "((【|（|)预付\\：?[\u4e00-\u9fa5]{0,}\\＋?[\u4e00-\u9fa5]{0,}(】|）|))";// 【预付】、（预付：酒店＋景点）
			regExs[2] = "((——|“”))";// “——”、““””
			regExs[3] = "(\\（\\d{1,}[\u4e00-\u9fa5]{1,}\\）)";// （3星标准）
			regExs[4] = "((\\？\\.|\\d{1,}㎡\\.))";// ？.及92㎡.
			for (String regEx : regExs) {
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(name);
				while (m.find()) {
					String temp = m.group();
					name = name.replace(temp, "");
				}
			}

			// 替换“.”为“•”
			// 还有把一些特殊字符如：★，●，?替换为•
			String regex = "((\\.|\\★|\\●|\\?))";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(name);
			while (m.find()) {
				String temp = m.group();
				name = name.replace(temp, "•");
			}
			to.put("name_chn", name);
			
			//名称中包含“（仅供测试）、（已售完）”等字样的poi全部滤除
			regex = "((测试|售完|暂无|装修))";
			p = Pattern.compile(regex);
			m = p.matcher(name);
			while (m.find()) {
				errMessage = "名称不对";
				errValue = from;
				return false;
			}
		}

		return true;
	}
}
