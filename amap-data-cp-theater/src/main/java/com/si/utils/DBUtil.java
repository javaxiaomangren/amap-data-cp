package com.si.utils;

import com.si.dao.GeneralizeDao;
import com.si.dao.MongoGeneralDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DBUtil {
	private final static String CONFIG = "classpath:applicationContext.xml";
	private final static String MONGO_DAO_PREFIX = "generallizeMongoDao";
	
	private final static String MYSQL_DAO_PREFIX = "generallizeMysqlDao";

	public synchronized static MongoGeneralDao getGenerallizeMongoDao() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG);
		MongoGeneralDao gdao = (MongoGeneralDao) ctx.getBean(MONGO_DAO_PREFIX);
		return gdao;
	}
	
	public synchronized static GeneralizeDao getGeneralizeMysqlDao() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG);
		GeneralizeDao gdao = (GeneralizeDao) ctx.getBean(MYSQL_DAO_PREFIX);
		return gdao;
	}
	
	public synchronized static MongoGeneralDao getGenerallizeMongoDao(String env) {
		String configfile = "classpath:applicationContext_"+env+".xml";
		ApplicationContext ctx = new ClassPathXmlApplicationContext(configfile);
		MongoGeneralDao gdao = (MongoGeneralDao) ctx.getBean(MONGO_DAO_PREFIX);
		return gdao;
	}
	

}
