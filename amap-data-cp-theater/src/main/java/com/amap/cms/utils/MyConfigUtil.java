package com.amap.cms.utils;

import com.amap.base.BusinessException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;

public class MyConfigUtil {
	private PropertiesConfiguration config;
	private String configFile;

	public MyConfigUtil(String fileName) {
		this.configFile = fileName;
		loadPros();
	}

	public void loadPros() {
		if (config == null) {
			try {
				config = new PropertiesConfiguration(configFile);
			} catch (ConfigurationException e) {
				throw new BusinessException("读取配置文件异常 ", e);
			}
		}
	}

	public Boolean getBoolean(String arg0, Boolean arg1) {
		return config.getBoolean(arg0, arg1);
	}

	public boolean getBoolean(String arg0) {
		return config.getBoolean(arg0);
	}

	public double getDouble(String arg0, double arg1) {
		return config.getDouble(arg0, arg1);
	}

	public double getDouble(String arg0) {
		return config.getDouble(arg0);
	}

	public float getFloat(String arg0, float arg1) {
		return config.getFloat(arg0, arg1);
	}

	public float getFloat(String arg0) {
		return config.getFloat(arg0);
	}

	public int getInt(String arg0, int arg1) {
		return config.getInt(arg0, arg1);
	}

	public int getInt(String arg0) {
		return config.getInt(arg0);
	}

	@SuppressWarnings("rawtypes")
	public List getList(String arg0, List arg1) {
		return config.getList(arg0, arg1);
	}

	@SuppressWarnings("rawtypes")
	public List getList(String arg0) {
		return config.getList(arg0);
	}

	public long getLong(String arg0, long arg1) {
		return config.getLong(arg0, arg1);
	}

	public long getLong(String arg0) {
		return config.getLong(arg0);
	}

	public short getShort(String arg0, short arg1) {
		return config.getShort(arg0, arg1);
	}

	public short getShort(String arg0) {
		return config.getShort(arg0);
	}

	public String getString(String arg0, String arg1) {
		return config.getString(arg0, arg1);
	}

	public String getString(String arg0) {
		return config.getString(arg0);
	}

	public String[] getStringArray(String arg0) {
		return config.getStringArray(arg0);
	}

	public void setValue(String key, String value) throws Exception {
		config.setProperty(key, value);
		config.save();
	}

}
