/**
 * 2013-5-27
 */
package com.amap.data.save.dianping;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.dianping.fieldMap.DeepMap;
import com.amap.data.save.dianping.fieldMap.MenuMap;
import com.amap.data.save.dianping.fieldMap.PicsMap;
import com.amap.data.save.transfer.Apitransfer;

public class DianpingApitransfer extends Apitransfer {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		 l.add(new PicsMap(templet));// 深度图片态息映射
		 l.add(new DeepMap(templet));// 深度最低价格映射
		 l.add(new MenuMap(templet)); 
		return l;
	}
}
