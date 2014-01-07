/**
 * 2013-5-10
 */
package com.amap.data.save.dianping.scenic;

import java.util.LinkedHashMap;

import com.amap.data.save.dianping.DianpingSave;

public class DianpingScenicSave extends DianpingSave {
	/**
	 * 拼装spec字段：把order_url从deep中拿出来，放进spec中；并且删除deep中的
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected LinkedHashMap combineSpecContent(LinkedHashMap combineMap,
			String cp) { 
		LinkedHashMap deep = (LinkedHashMap) combineMap.get("deep");
		Object order_url = deep.get("order_url");
		deep.remove("order_url");
		combineMap.put("deep", deep);
		if(order_url != null && !order_url.equals("")){
			LinkedHashMap spec = new LinkedHashMap();
			LinkedHashMap m = new LinkedHashMap();
			order_url = order_url + "&s=gaode";
			m.put("order_url", order_url);
			spec.put(cp, m);
			combineMap.put("spec", spec);
		}
		return combineMap;
	}
}
