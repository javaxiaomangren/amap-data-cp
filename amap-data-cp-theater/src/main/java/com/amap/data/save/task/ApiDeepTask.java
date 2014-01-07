/**
 * @caoxuena
 * 2013-4-3
 *ApiTask.java
 */
package com.amap.data.save.task;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.TempletConfig;
import com.amap.data.save.transfer.Apitransfer;

public class ApiDeepTask {
	protected static final Logger log = LoggerFactory
			.getLogger(ApiDeepTask.class);
	protected TempletConfig templet;
	protected Apitransfer transfer;

	protected List<String> timeFieldList;

	public ApiDeepTask(TempletConfig templet, Apitransfer transfer) {
		this.templet = templet;
		this.transfer = transfer;
	}

	public void init() {
		transfer.init(templet);
	}

	@SuppressWarnings("rawtypes")
	public Map toApiTranser(String json) {
		return transfer.transfer(json);
	}
}
