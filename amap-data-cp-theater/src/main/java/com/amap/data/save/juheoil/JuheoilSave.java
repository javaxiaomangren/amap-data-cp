/**
 * 2013-5-23
 */
package com.amap.data.save.juheoil;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.Save;
import com.amap.data.save.task.ApiRtiTask;
import com.amap.data.save.transfer.ApiRtitransfer;

public class JuheoilSave extends Save{
	@Override
	public void init(String cp) {
		// 处理动态信息
		TempletConfig rtitemplet = new TempletConfig(cp + "_rti_1");
		ApiRtitransfer rtiTranfer = new JuheoilApiRtitransfer();
		rtitask = new ApiRtiTask(rtitemplet, rtiTranfer);
		rtitask.init();
	}
}