package com.amap.data.util.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DBUtil {
	private final static String CONFIG = "classpath:applicationContext.xml";
	private final static String DAO_PREFIX = "generalDao";

	public synchronized static GeneralDao getGeneralizeDao() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG);
		GeneralDao gdao = (GeneralDao) ctx.getBean(DAO_PREFIX);
		return gdao;
	}

}
