package com.amap.data.base.fieldmap;

import java.util.Map;

import com.amap.base.utils.LogParseUtil;
import com.amap.base.utils.MD5Util;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class SrcidMap extends FieldMap {
	private String srcid_gen = null;
	private String srcid_map = null;
	private String srcid_extra_reg = null;
	private String srcid_last=null;
	public SrcidMap(TempletConfig templet) {
		type= "来源id映射";
		
		srcid_gen = templet.getString("srcid_gen");
		srcid_map = templet.getString("srcid_map");
		srcid_last = templet.getString("srcid_last");
		if ("extra".equals(srcid_gen)) {
			srcid_extra_reg = templet.getString("srcid_extra_reg");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		if ("extra".equals(srcid_gen)) {
			String t = (String) from.get(srcid_map);
			if((t.equals("")||t==null)&&srcid_last!=null){
				t=(String) from.get(srcid_last);
			}
			Map m = LogParseUtil.logParseToMap(t, srcid_extra_reg);

			if (!(m.get("srcid") == null || "".equals(m.get("srcid")))) {
				to.put("srcid", m.get("srcid"));
				return true;
			} else {
				errMessage = "配置的extra,获取不到srcid的值";
				errValue = from;
				return false;
			}
		} else if ("quote".equals(srcid_gen)) {// 增加直接获取srcid类型
			String t = (String) from.get(srcid_map);

			if (t != null || "".equals(t)) {
				to.put("srcid", t);
				return true;
			} else {
				errMessage = "配置的quoto,获取不到srcid的值";
				errValue = from;
				return false;
			}
		} else if ("md5".equals(srcid_gen)) {// 增加取字段MD5码类型
			String t = (String) from.get(srcid_map);

			if (t != null || "".equals(t)) {
				MD5Util md5 = new MD5Util();
				to.put("srcid", md5.getMD5ofStr(t));
				return true;
			} else {
				errMessage = "配置的md5,获取不到srcid的值";
				errValue = from;
				return false;
			}
		}

		return false;
	}

}
