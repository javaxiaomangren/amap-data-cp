package com.amap.theater.tableInterface;

import com.amap.base.data.DBDataReader;

import java.util.Map;

public class GetPoiidFromCpCpid {
	
	private String deepTable = "poi_deepinfo";
	/**
	 * 通过给定的cp和cpid获取对应的poiid，如果poiid为空，则返回null
	 */
	@SuppressWarnings("rawtypes")
	public Object getPoiidFromCpCpid(String cp, String cpid){
		Object poiid = null;
		String sql = "SELECT poiid FROM " + deepTable + " WHERE cp = '" + cp + "' AND id = '" + cpid + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		poiid = ((Map)ddr.readSingle()).get("poiid");
		return poiid;
	}
}
