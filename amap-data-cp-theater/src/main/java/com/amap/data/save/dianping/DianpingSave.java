/**
 * 2013-5-10
 */
package com.amap.data.save.dianping;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.Save;
import com.amap.data.save.task.ApiDeepTask;
import com.amap.data.save.task.ApiRtiTask;
import com.amap.data.save.transfer.ApiRtitransfer;
import com.amap.data.save.transfer.Apitransfer;

public class DianpingSave extends Save {
	@Override
	public void init(String cp) {
		// 初始化深度处理程序
		TempletConfig deeptemplet = new TempletConfig(cp + "_deep_1");
		Apitransfer tranfer = new DianpingApitransfer();
		deeptask = new ApiDeepTask(deeptemplet, tranfer);
		deeptask.init();

		// 处理动态信息
		TempletConfig rtitemplet = new TempletConfig("dianping_api_rti_1");
		ApiRtitransfer rtiTranfer = new DianpingApiRtitransfer();
		rtitask = new ApiRtiTask(rtitemplet, rtiTranfer);
		rtitask.init();
	}
}
