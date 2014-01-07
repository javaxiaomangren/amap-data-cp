package com.amap.data.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.amap.base.BusinessException;
import com.amap.base.utils.ConfigUtil;

public class TempletConfig {
	private Configuration conf;
	
	//定义update_flag的值：0保持不变；1新增
	public static final int KEEP = 0;
	public static final int UPDATE = 1;

	public TempletConfig(String srctype) {
		String templet = ConfigUtil.getString("templetpath") + srctype
				+ ".properties";
		try {
			conf = new PropertiesConfiguration(templet);
		} catch (ConfigurationException e) {
			throw new BusinessException("读取配置文件异常", e);
		}
	}

	public String getString(String field) {
		return conf.getString(field);
	}

	@SuppressWarnings("rawtypes")
	public List getList(String field) {
		return conf.getList(field);
	}

	public void setStringField(String field, String value) {
		conf.setProperty(field, value);
	}

	public static void main(String[] args) throws IOException {
 		TempletConfig tc = new TempletConfig("theater_damai_api_deep_1");
		tc.setStringField("aa", "bb");
        System.out.println(tc.getString("api_charset"));
    }
}
