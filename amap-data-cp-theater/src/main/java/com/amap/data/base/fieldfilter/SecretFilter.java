package com.amap.data.base.fieldfilter;

import java.util.List;
import java.util.Map;

import com.amap.base.data.FileDataReader;
import com.amap.base.utils.ConfigUtil;
import com.amap.data.base.FieldFilter;
import com.amap.data.base.TempletConfig;

@SuppressWarnings("unchecked")
public class SecretFilter extends FieldFilter {
	private static List<String> secretKeyWordList;
	private static List<String> whiteList;

	static {
		try {
			FileDataReader fdr = new FileDataReader(
					ConfigUtil.getString("secret_key_word_file"), "gbk");
			secretKeyWordList = fdr.readAll();
			
			fdr = new FileDataReader(ConfigUtil.getString("white_list_file"),
					"gbk");
			whiteList = fdr.readAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String nameField = null;
	private String addrField = null;

	public SecretFilter(TempletConfig templet) {
		type = "涉密过滤";
		nameField = templet.getString("secret_filter_name_field");
		addrField = templet.getString("secret_filter_addr_field");
	}

	@SuppressWarnings("rawtypes")
	public boolean fieldfilter(Map from) {
		String name = (String) from.get(nameField);
		String addr = (String) from.get(addrField);
		boolean flag = !SecretFilter.isSecret(name)
				&& !SecretFilter.isSecret(addr);
		if (flag == false) {
			errMessage = type;
			errValue.put(nameField, from.get(nameField));
			errValue.put(addrField, from.get(addrField));
		}
		return flag;
	}

	private static boolean isSecret(String str) {
		if (str == null)
			return false;
		
		for (String s : secretKeyWordList) {
			s = s.replace("&", ".*").replace("番号+", "[0-9零一二三四五六七八九]");
			if (str.contains(s) || str.matches(s)) {
				for (String s1 : whiteList) {
					if (str.contains(s1))
						return false;
				}
				return true;
			}
		}
		return false;
	}

}
