/**
 * 2013-5-23
 */
package com.amap.data.save.baidu;

import com.amap.data.save.Save;

/**
 * 阿里几个只有基础信息的新增数据上线
 */
public class QingbaoAddSave extends Save{
	@Override
	public void init(String cp) {
	}
	
	/**
	 * 判断新增数据是否能上线，不能的话，返回false；
	 * 百度数据如果是地名地址等信息，大类属于19就不上线新增
	 * 10.08add:"[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"16\",\"17\"]"
	 * 不在该范围内的，不推送 
	 * 12.04:19大类推送上线
	 */
	
	@Override
	public boolean assertAddRegular(String combineJson){
		if(combineJson.contains("\"new_type\" : \"0")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"10")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"11")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"12")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"13")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"14")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"16")){
			return true;
		} else if (combineJson.contains("\"new_type\" : \"17")){
			return true;
		}  else if (combineJson.contains("\"new_type\" : \"19")){
			return true;
		} else {
			return false;
		}
	}
}