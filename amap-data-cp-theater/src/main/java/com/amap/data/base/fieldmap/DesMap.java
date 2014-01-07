package com.amap.data.base.fieldmap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

//caoxuena：函数功能为替换描述信息中的非法及无用字段
public class DesMap extends FieldMap {
	private String fm;
	
	//定义门限：如果des长度小于该门限，则置des为null
	private static int desLength = 20;

	public DesMap(TempletConfig templet) {
		type = "des_map映射";

		fm = templet.getString("des_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		if (fm != null) {
			String des = (String) from.get("des");
			if(des != null && des.length() < desLength){
				des = null;
			}
			
			if(des != null){
				//删除汉字字符间的无用空格
				String[] regExs = new String[3];
				regExs[0] = "([\u4e00-\u9fa5]{1,}(\\　|\\ )+[\u4e00-\u9fa5]{1,})";
				regExs[1] = "((\\，|\\,|\\。|\\.|\\；|\\、|\\？)(\\　|\\ )+[\u4e00-\u9fa5]{1,})";
				regExs[2] = "([\u4e00-\u9fa5]{1,}(\\　|\\ )+(\\，|\\,|\\。|\\.|\\；|\\、|\\？))";
				for(String regEx : regExs){
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(des);
					while (m.find()) {
						String temp = m.group();
						String temp1 = temp.replace("　", "");
						temp1 = temp1.replace(" ", "");
						des = des.replace(temp, temp1);
						
						p = Pattern.compile(regEx);
						m = p.matcher(des);
					}
				}
				
				//■ ◆ ◇ ★ ◎ 统一替换为●
				if(des != null){
					String regex = "((\\■ \\◆\\◇\\★\\◎)+)";
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(des);
					while (m.find()) {
						String temp = m.group();
						des = des.replace(temp, "●");
					}
				}
				//替换描述信息中的？
				if(des.contains("?")){
					des = des.replace("?", "•");
				}
			}
			to.put("des", des);
		}
		return true;
	}
}
