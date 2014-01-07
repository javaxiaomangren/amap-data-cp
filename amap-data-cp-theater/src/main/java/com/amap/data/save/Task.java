package com.amap.data.save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Task {
	private static final Logger log = LoggerFactory.getLogger(Task.class);
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath:profiles/production/applicationContext-quartz.xml");
		log.info("定时任务执行了");
	}
}
