/**
 * 2013-5-15
 */
package com.amap.data.save.tuniu;

import java.util.LinkedHashMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.Save;
import com.amap.data.save.task.ApiDeepTask;
import com.amap.data.save.transfer.Apitransfer;

public class TuniuSave extends Save {
	@Override
	public void init(String cp) {
		// 初始化深度处理程序
		TempletConfig deeptemplet = new TempletConfig(cp + "_deep_1");
		Apitransfer tranfer = new TuniuApitransfer();
		deeptask = new ApiDeepTask(deeptemplet, tranfer);
		deeptask.init();
	}
	
	/**
	 * 拼装spec字段：把order_url从deep中拿出来，放进spec中；并且删除deep中的
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected LinkedHashMap combineSpec(LinkedHashMap combineMap,
			String cp) { 
		LinkedHashMap deep = (LinkedHashMap) combineMap.get("deep");
		Object order_url = deep.get("order_url");
		deep.remove("order_url");
		combineMap.put("deep", deep);
		if(order_url != null && !order_url.equals("")){
			LinkedHashMap spec = new LinkedHashMap();
			LinkedHashMap m = new LinkedHashMap();
			m.put("order_url", order_url);
			spec.put(cp, m);
			combineMap.put("spec", spec);
		}
		return combineMap;
	}
}
