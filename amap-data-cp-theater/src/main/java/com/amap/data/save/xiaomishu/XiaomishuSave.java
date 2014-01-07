/**
 * 2013-5-23
 */
package com.amap.data.save.xiaomishu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.save.Save;
import com.amap.data.save.SaveHelper;
import com.mongodb.util.JSON;

public class XiaomishuSave extends Save{
	@Override
	public void init(String cp) {
	}
	
	//只有有效可订餐时，才拼装from和idDictionaries，否则只拼装from信息
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected String getCombineJson(Object poiid, List<Map> ids, String cp,
			boolean hasDeep) {
		LinkedHashMap combineMap = new LinkedHashMap();
		combineMap.put("poiid", poiid);
		//获取深度信息
		Map deep = getNewestDeep(combineMap, ids, cp);
		Object id = deep.get("id");
		
		Map result = new HashMap();
		result.put("cp", cp);
		result.put("id", id);
		result.put("update_flag", deep.get(update_flag));
		
		combineMap.put("from", SaveHelper.getFrom(result, "dining_xiaomishu"));
		if(assertValid(deep)){
			//获取base字段，直接从deep表中取数据即可
			List<Map> validIds = getValidIds(combineMap, ids, cp);
			combineMap = combineBase(combineMap, validIds, cp);
			
			//深度有效的话，才拼装idDictionaries
			combineMap = combineidDictionaries(combineMap, "dining_xiaomishu");
			
			//深度有效的情况下，拼装spec字段13.08.01：xuena add
			combineMap = getSpecMap(combineMap, deep);
		}
		return JSON.serialize(combineMap);
	}
	/**
	 * 拼装spec字段
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getSpecMap(LinkedHashMap combineMap, Map deep){
		Map deepinfoMap = (Map) JSON.parse(deep.get("deep").toString());
		Map deepMap = (Map) JSON.parse(deepinfoMap.get("deep").toString());
		
		Map temp = new HashMap();
		Object discountinfo = deepMap.get("discountinfo");
		if(discountinfo != null && (discountinfo + "").equals("1")){
			discountinfo = null;
		}
		temp.put("discountinfo", discountinfo);
		
		Map xiaomishu = new HashMap();
		xiaomishu.put("dining_xiaomishu", temp);
		
		combineMap.put("spec", xiaomishu);
		return combineMap;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Map getValidDeep(List<Map> deeps){
		//首先判断是否存在都有效的深度信息
		Object assertDeep = assertValidDeep(deeps);
		Map newestDeep = deeps.get(0);
		if(assertDeep != null){
			//存在有效的深度信息，选择最新的有效深度
			newestDeep = (Map) assertDeep;
			for(Map deep : deeps){
				if(assertValid(deep) && deep.get("updatetime")
						.toString()
						.compareToIgnoreCase(
								newestDeep.get("updatetime").toString()) > 0){
					newestDeep = deep;
				}
			}
		}else{
			for (Map deep : deeps) {
				if (deep.get("updatetime")
						.toString()
						.compareToIgnoreCase(
								newestDeep.get("updatetime").toString()) > 0) {
					newestDeep = deep;
				}
			}
		}
		
		return newestDeep;
	}
	
	//判断是否存在都有效的深度信息
	@SuppressWarnings("rawtypes")
	private Object assertValidDeep(List<Map> deeps){
		for(Map deep : deeps){
			if(assertValid(deep)){
				return deep;
			}
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean assertValid(Map deep){
		Map deepinfoMap = (Map) JSON.parse(deep.get("deep").toString());
		Map deepMap = (Map) JSON.parse(deepinfoMap.get("deep").toString());
		Object status = deepMap.get("status");
		Object IsCanBooking = deepMap.get("IsCanBooking");
		if(status != null && Integer.parseInt(status.toString()) == 1 && IsCanBooking != null && IsCanBooking.equals("True")){
			return true;
		}
		return false;
	}
}