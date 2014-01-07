/**
 * @caoxuena
 * 2013-4-3
 *DamaiDeepApiTransfer.java
 */
package com.amap.data.save.theater;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.theater.fieldMap.DeepPicMap;
import com.amap.data.save.transfer.Apitransfer;

public class DamaiDeepApiTransfer extends Apitransfer{
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		l.add(new DeepPicMap(templet));//深度图片信息映射
		return l;
	}

}
