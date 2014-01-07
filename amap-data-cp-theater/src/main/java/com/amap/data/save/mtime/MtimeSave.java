/**
 * 2013-5-15
 */
package com.amap.data.save.mtime;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.Save;
import com.amap.data.save.task.ApiDeepTask;
import com.amap.data.save.transfer.Apitransfer;

public class MtimeSave extends Save {
	@Override
	public void init(String cp) {
		// 初始化深度处理程序
		TempletConfig deeptemplet = new TempletConfig(cp + "_deep_1");
		Apitransfer tranfer = new MtimeDeepApiTransfer();
		deeptask = new ApiDeepTask(deeptemplet, tranfer);
		deeptask.init();
	}
}
