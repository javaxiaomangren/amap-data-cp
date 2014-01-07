/**
 * 2013-5-9
 */
package com.amap.data.save.elong;

import java.io.File;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.save.FileLocker;

public class ElongSaveMain {
	private final static Logger log = LoggerFactory
			.getLogger(ElongSaveMain.class);
	private static File file = null;
	/**
	 * 每隔一段时间执行一次save接口;
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		String path = this.getClass().getResource("/").getPath();
		// 判断指定路径下是否已经有对应的文件了
		if (file != null && file.exists()) {
			log.info("指定文件已经存在");
		} else {
			log.info("指定文件不存在，第一次创建");
			file = new File(path + "file.txt");
			file.createNewFile();
		}
		try {
			// 添加文件锁
			FileLock lock = FileLocker.getFileLock(file);
			log.info("执行任务成功--开始执行");
			boolean flag = true;
			for (int i = 0; i < 3; i++) {
				try {
					new ElongSave().dealSave("hotel_elong_api", true);
				} catch (Exception e) {
					log.info(e.toString());
					flag = false;
				}
				if (flag) {
					i = 3;
				}
			}
			log.info("执行任务成功--本次任务成功执行");
			lock.release();
		} catch (Exception e) {
			log.info("执行任务失败--有另一个程序在运行中");
		}
	}

	public static void main(String[] args) {
		try {
			new ElongSave().dealSave("hotel_elong_api", true);
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
}
