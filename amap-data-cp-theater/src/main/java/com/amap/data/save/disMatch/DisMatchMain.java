/**
 * 2013-7-17
 */
package com.amap.data.save.disMatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisMatchMain {
	private final static Logger log = LoggerFactory.getLogger(DisMatchMain.class);
	public void run() {
		log.info("本次误匹配开始执行！！！！！");
		try {
			DisMatch.deal();
		} catch (Exception e) {
			log.info(e.toString());
		}
		log.info("本次误匹配执行结束！！！！！");
	}
	
	public static void main(String[] args){
		try {
			DisMatch.deal();
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
}
