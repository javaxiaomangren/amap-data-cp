package com.amap.cms.utils;

/**
 *poi基础数据入库进程开关
 * @author v-helianxin
 *
 */
public class SingleNum {
	private static Integer Poinum=1;

	public static Integer getPoinum() {
		return Poinum;
	}

	public static void setPoinum(Integer poinum) {
		Poinum = poinum;
	}
 
  
}



