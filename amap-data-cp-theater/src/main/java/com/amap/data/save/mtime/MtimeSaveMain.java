/**
 * 2013-5-16
 */
package com.amap.data.save.mtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.cms.utils.MyConfigUtil;

public class MtimeSaveMain {
	private final static Logger log = LoggerFactory.getLogger(MtimeSaveMain.class);
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
			System.exit(0);
		} else {
			log.info("执行任务成功--开始执行");
			recordConfig_matrix.setValue("error_status", "run");
			try {
				new MtimeSave().dealSave("cinema_mtime_api", true);
			} catch (Exception e) {
				log.info(e.toString());
			}
			recordConfig_matrix.setValue("error_status", "star");
		}
	}

	public static void main(String[] args) {

		try {
			new MtimeSave().dealSave("cinema_mtime_api", true);
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
}
