package com.amap.data.base.fieldmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.util.telproc.ExtractTelephones;

public class TelMap extends FieldMap {

	private Map<String, String> map = new HashMap<String, String>();

	@SuppressWarnings("rawtypes")
	public TelMap(TempletConfig templet) {
		type= "电话号码处理";
		List fm = templet.getList("tel_map");
		for (Object o : fm) {
			String s = (String) o;
			String[] t = s.split("-");
			map.put(t[0], t[1]);
		}
	}

	/**
	 * @param args
	 */


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean fieldmap(Map from, Map to) {
		
		for (String s : map.keySet()) {
			String telNum = (String)from.get(map.get(s));
			if (telNum != null){
				String newTelNum = ExtractTelephones.extractTelephone(telNum);
				to.put(s, newTelNum);
			}			
		}
		return true;
	}
}
