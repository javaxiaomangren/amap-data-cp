package com.amap.data.base.fieldmap;

import java.util.Map;

import com.amap.base.utils.IdGenUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class AidMap extends FieldMap {
	private String aid_gen = null;

	public AidMap(TempletConfig templet) {
		type= "aid生成映射";
		aid_gen = templet.getString("aid_gen");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		if ("idgen".equals(aid_gen)) {
			to.put("aid", IdGenUtil.genSnowflakeId());
			return true;
		}else{
			errMessage = "生成aid的配置未配置正确";
			errValue = from;
			return false;
		}
		
	}
}
