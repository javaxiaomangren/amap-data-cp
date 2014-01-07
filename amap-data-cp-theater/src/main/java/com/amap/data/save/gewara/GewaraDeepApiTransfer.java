/**
 * 2013-5-16
 */
package com.amap.data.save.gewara;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.gewara.fieldMap.IntroMap;
import com.amap.data.save.transfer.Apitransfer;

public class GewaraDeepApiTransfer extends Apitransfer{
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		l.add(new IntroMap(templet));//简介信息映射
		return l;
	}

}