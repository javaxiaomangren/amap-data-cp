/**
 * 2013-5-16
 */
package com.amap.data.save.cinema;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.FieldMap;
import com.amap.data.save.cinema.fieldMap.PicsMap;
import com.amap.data.save.transfer.Apitransfer;

public class CinemaDeepApiTransfer extends Apitransfer{
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<FieldMap> specMap() {
		List l = new ArrayList<FieldMap>();
		l.add(new PicsMap(templet));//图片信息映射
		return l;
	}

}