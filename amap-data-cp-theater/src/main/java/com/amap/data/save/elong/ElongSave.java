/**
 * 2013-5-27
 */
package com.amap.data.save.elong;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.Save;
import com.amap.data.save.SaveHelper;
import com.amap.data.save.task.ApiDeepTask;
import com.amap.data.save.task.ApiRtiTask;
import com.amap.data.save.transfer.ApiRtitransfer;
import com.amap.data.save.transfer.Apitransfer;
import com.mongodb.util.JSON;

public class ElongSave extends Save{
	@Override
	public void init(String cp) {
		// 初始化深度处理程序
		TempletConfig deeptemplet = new TempletConfig(cp + "_deep_1");
		Apitransfer tranfer = new ElongApitransfer();
		deeptask = new ApiDeepTask(deeptemplet, tranfer);
		deeptask.init();

		// 处理动态信息
		TempletConfig rtitemplet = new TempletConfig(cp + "_rti_1");
		ApiRtitransfer rtiTranfer = new ElongApiRtitransfer();
		rtitask = new ApiRtiTask(rtitemplet, rtiTranfer);
		rtitask.init();
	}
	
	/**
	 * 判断当前rti数组是否可用，不可用的删除，返回部分只包括可用的rti信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<Map> getValidRti(Map rti) {
		Object rtiInfo = rti.get("rti");
		List<Map> rtiMaps = (List<Map>) JSON.parse(SaveHelper
				.getTranserJson(rtiInfo));
		List<Map> maps = new ArrayList<Map>();
		for (Map rtiMap : rtiMaps) {
			Object review_num = rtiMap.get("review_num");
			if(review_num == null || review_num.equals("0")){
				continue;
			}
			rtiMap.put("cp", rti.get("cp"));
			rtiMap.put("id", rti.get("id"));
			maps.add(rtiMap);
		}
		return maps;
	}
}
