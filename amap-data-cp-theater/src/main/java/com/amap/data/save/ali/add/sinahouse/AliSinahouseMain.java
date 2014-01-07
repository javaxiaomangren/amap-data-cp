/**
 * 2013-5-23
 */
package com.amap.data.save.ali.add.sinahouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.cms.utils.MyConfigUtil;
import com.amap.data.save.add.AddSave;

public class AliSinahouseMain {
	private final static Logger log = LoggerFactory.getLogger(AliSinahouseMain.class);
	private static MyConfigUtil recordConfig_matrix = new MyConfigUtil(
			"matrix.properties");
			
	public void run() throws Exception {
		String error_status = recordConfig_matrix.getString("error_status");
		if (error_status.equals("run")) {
			log.info("执行任务失败--有另一个程序在运行中");
		} else {
			log.info("执行任务成功--开始执行");
			recordConfig_matrix.setValue("error_status", "run");
			try {
				new AddSave().dealSave("ali_sinahouse", false);
			} catch (Exception e) {
				log.info(e.toString());
			}
			recordConfig_matrix.setValue("error_status", "star");
		}
	}
	
	public static void main(String[] args){
		try {
			new AddSave().dealSave("ali_sinahouse", false);
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
}
