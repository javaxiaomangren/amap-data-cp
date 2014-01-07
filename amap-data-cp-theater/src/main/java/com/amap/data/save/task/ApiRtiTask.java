/**
 * @caoxuena
 * 2013-4-3
 *ApiRtiTask.java
 *动态信息需要组装，根据更新的场馆id找到对应所有的动态信息，并组装成list
 */
package com.amap.data.save.task;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.transfer.ApiRtitransfer;

public class ApiRtiTask {
	protected static final Logger log = LoggerFactory
			.getLogger(ApiRtiTask.class);
	protected TempletConfig templet;
	protected ApiRtitransfer transfer;

	protected String rtiTable;

	protected List<String> timeFieldList;

	public ApiRtiTask(TempletConfig templet, ApiRtitransfer transfer) {
		this.templet = templet;
		this.transfer = transfer;
	}

	public void init() {
		transfer.init(templet);
		rtiTable = templet.getString("to_table_rti");
	}

	@SuppressWarnings("rawtypes")
	public Map toApiTranser(List<Map> rtis) {
		if (rtis == null || rtis.size() == 0) {
			return null;
		}
		return transfer.transfer(rtis);
	}
}
