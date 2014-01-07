package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

//caoxuena：函数功能为组合拼接name_all字段
//caoxuena：新增功能：如果含有分店名，则处理后结果格式为：name_chn（分店名）
public class NameAllMap extends FieldMap {
	private Map<String, String> nameAllMap = new HashMap<String, String>();

	@SuppressWarnings("rawtypes")
	public NameAllMap(TempletConfig templet) {
		type= "名字name_all映射";
		
		List fm = templet.getList("name_map");
		for (Object o : fm) {
			String s = (String) o;
			String[] t = s.split("-");
			nameAllMap.put(t[0], t[1]);
			//System.out.println(nameAllMap);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		String name = ObjectUtil.toString(from.get(nameAllMap.get("name_chn")));
		
		//判断是否包含分店，对分店进行处理
		if (nameAllMap.containsKey("fendian")){
			String fendian = ObjectUtil.toString(from.get(nameAllMap.get("fendian")));
			name += "（" + fendian + "）";
		}
		
		String nameAll = "";

		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add(name);
		
		String alias = null;
		if (nameAllMap.containsKey("alias")) {
			alias = ObjectUtil
					.toString(from.get(nameAllMap.get("alias")));
			//cxn add:deal with alias when alias contains more than two names
			
			String[] fields = alias.split(";");
			for(int i = 0; i < fields.length; i++){
				if(!fields[i].equals("") && !nameList.contains(fields[i])){
					//剔除别中的汉字“别名：”和（）
					if(fields[i].contains("别名：")){
						fields[i] = fields[i].replace("别名：", "");
					}
					if(fields[i].contains("（") && fields[i].contains("）")){
						fields[i] = fields[i].replace("（", "");
						fields[i] = fields[i].replace("）", "");
					}
					nameList.add(fields[i]);
				}
			}
			
		}
		
		String engName = null;
		if (nameAllMap.containsKey("engname")) {
			engName = ObjectUtil
					.toString(from.get(nameAllMap.get("engname")));
			String[] fields = engName.split(";");
			for(int i = 0; i < fields.length; i++){
				if(!fields[i].equals("") && !nameList.contains(fields[i])){
					nameList.add(fields[i]);
				}
			}
		}
		
		//get the name_all
		for(int i = 0; i < nameList.size(); i++){
			if(i != 0){
				nameAll += ";";
			}
			nameAll += nameList.get(i);
		}
		
		//record
		to.put("name_chn", name);
		to.put("name_chn_all", nameAll);
		return true;
	}
}
