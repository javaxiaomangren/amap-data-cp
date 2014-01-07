/**
 * 2013-5-16
 */
package com.amap.data.save.roadcross;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.cms.utils.MyConfigUtil;

public class RoadcrossSaveMain {
	private final static Logger log = LoggerFactory.getLogger(RoadcrossSaveMain.class);
	private static MyConfigUtil recordConfig_matrix = new MyConfigUtil(
			"matrix.properties");
			

	/**
	 * 每隔一段时间执行一次save接口;
	 * @throws Exception 
	 */
	public void run() throws Exception {
		String error_status = recordConfig_matrix.getString("error_status");
		if (error_status.equals("run")) {
			log.info("执行任务失败--有另一个程序在运行中");
		} else {
			log.info("执行任务成功--开始执行");
			recordConfig_matrix.setValue("error_status", "run");
			try {
				new RoadcrossSave().dealSave("road_cross", false);
			} catch (Exception e) {
				log.info(e.toString());
			}
			recordConfig_matrix.setValue("error_status", "star");
		}
	}

	public static void main(String[] args) {
		try {
			new RoadcrossSave().dealSave("road_cross", false);
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
}
