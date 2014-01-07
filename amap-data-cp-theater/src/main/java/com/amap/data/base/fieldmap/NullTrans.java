/**
 * @author caoxuena
 *2012-12-18
 */
package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class NullTrans extends FieldMap {

	private List<String> nullMap = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public NullTrans(TempletConfig templet) {
		// null_trans_map=name,intro
		nullMap = templet.getList("null_trans_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		//遍历需要进行空字符串转换的各字段
		for(int i = 0; i < nullMap.size(); i++){
			String ziduanName = nullMap.get(i);
			Object o = from.get(ziduanName);
			
			String ziduanInfo = null;
			
			if(o != null){
				ziduanInfo = o.toString();
				if(ziduanInfo.equalsIgnoreCase("") || ziduanInfo.equalsIgnoreCase("null")){
					ziduanInfo = null;
				}
			}
			to.put(ziduanName, ziduanInfo);
		}
		return true;
	}


	@Override
	public String getType() {
		return "空字符串替换错误";
	}
}
