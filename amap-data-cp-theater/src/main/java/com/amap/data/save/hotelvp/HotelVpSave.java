/**
 * 2013-5-27
 */
package com.amap.data.save.hotelvp;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.Save;
import com.amap.data.save.task.ApiDeepTask;
import com.amap.data.save.transfer.Apitransfer;

public class HotelVpSave extends Save{
	@Override
	public void init(String cp) {
		// 初始化深度处理程序
		TempletConfig deeptemplet = new TempletConfig(cp + "_deep_1");
		Apitransfer tranfer = new HotelVpApitransfer();
		deeptask = new ApiDeepTask(deeptemplet, tranfer);
		deeptask.init();
	}
}
