/**
 * 2013-5-23
 */
package com.amap.data.save.diandao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.save.Save;
import com.amap.data.save.SaveHelper;
import com.mongodb.util.JSON;

public class DiandaoSave extends Save{
	@Override
	public void init(String cp) {
	}
	
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
		
		combineMap.put("from", SaveHelper.getFrom(result, cp));
		if(assertValid(deep)){
			//深度有效的话，才拼装idDictionaries
			combineMap = combineidDictionaries(combineMap, deep, cp);
		}
		return JSON.serialize(combineMap);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected LinkedHashMap combineidDictionaries(LinkedHashMap combineMap, Map deep,
			String cp) {
		if(deep.get("deep") != null && !deep.get("deep").equals("")){
			Map deepinfo = (Map) JSON.parse(deep.get("deep").toString());
			Map idDictionaries = (Map) JSON.parse(deepinfo.get("idDictionaries").toString());
			combineMap.put("idDictionaries", idDictionaries);
		}
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
		if(deep.get("deep") == null || deep.get("deep").equals("")){
			return true;
		}
		Map deepinfoMap = (Map) JSON.parse(deep.get("deep").toString());
		Object status = deepinfoMap.get("status");
		if(status != null && "-1".equals(status + "")){
			return false;
		}
		return true;
	}
}