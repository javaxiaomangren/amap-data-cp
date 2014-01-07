/**
 * @caoxuena
 * 2013-4-3
 *ApiRtitransfer.java
 */
package com.amap.data.save.transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.data.base.FieldCheck;
import com.amap.data.base.FieldFilter;
import com.amap.data.base.FieldMap;
import com.mongodb.util.JSON;

public class ApiRtitransfer extends Apitransfer{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
			.getLogger(ApiRtitransfer.class);
	
	@SuppressWarnings("rawtypes")
	protected List<Map> rtiMaps;
	protected List<String> rtiColList;

	@SuppressWarnings("unchecked")
	@Override
	public void initSeqField() {
		rtiColList = templet.getList("rti_cols");
	}

	//判断当前处理的动态信息是否过期，如果是的话 则不处理
	@SuppressWarnings("rawtypes")
	protected boolean assertIsOverDate(Map rti){
		return false;
	}
	@SuppressWarnings("rawtypes")
	public Map transfer(List<Map> rtis) {
		rtiMaps = new ArrayList<Map>();
		//分别处理获取各个rti信息
		for (Map rti : rtis) {
			if(assertIsOverDate(rti)){
				continue;
			}
			fMap = rti;
			// 字段转换
			transField();
		}
		//组装rti信息
		combineRtis();
		return tMap;

	}

	//获取组装在一起的rti的完整字段信息
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private void combineRtis(){
		Object cp = tMap.get("cp");
		tMap = new HashMap();
		tMap.put("id", fMap.get("id"));
		tMap.put("cp",cp);
		tMap.put("update_flag", templet.UPDATE);
		tMap.put("rti", JSON.serialize(rtiMaps));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void transField() {

		boolean f;
		Map mt = new HashMap();
		Map m = new HashMap();
		m.putAll(fMap);

		// 特殊过滤
		if (!CollectionUtils.isEmpty(specFieldFilter)) {
			for (FieldFilter fieldFilter : specFieldFilter) {
				f = fieldFilter.fieldfilter(m);
				// 错误
				if (f == false) {
					addToError(m, fieldFilter);
					// 如果是错误，跳出
					if (fieldFilter.getIsWarning() != true) {
						return;
					}
				}
			}
		}

		// 过滤
		if (!CollectionUtils.isEmpty(commonFieldFilter)) {
			for (FieldFilter fieldFilter : commonFieldFilter) {
				f = fieldFilter.fieldfilter(m);
				// 错误
				if (f == false) {
					addToError(m, fieldFilter);
					// 如果是错误，跳出
					if (fieldFilter.getIsWarning() != true) {
						return;
					}
				}
			}
		}

		// 特有 映射
		if (!CollectionUtils.isEmpty(specFieldMap)) {
			for (FieldMap fm : specFieldMap) {
				f = fm.fieldmap(m, mt);
				// 错误
				if (f == false) {
					addToError(m, fm);
					// 如果是错误，跳出
					if (fm.getIsWarning() != true) {
						return;
					}
				}
				m.putAll(mt);
			}
		}
		// 映射
		if (!CollectionUtils.isEmpty(commonFieldMap)) {
			for (FieldMap fm : commonFieldMap) {
				f = fm.fieldmap(m, mt);
				// 错误
				if (f == false) {
					addToError(m, fm);
					// 如果是错误，跳出
					if (fm.getIsWarning() != true) {
						return;
					}
				}
				m.putAll(mt);
			}
		}

		// 检查
		if (!CollectionUtils.isEmpty(commonFieldCheck)) {
			for (FieldCheck fc : commonFieldCheck) {

				f = fc.fieldsCheck(mt);
				// 错误
				if (f == false) {
					addToError(m, fc);
					if (fc.getIsWarning() != true) {
						return;
					}
				}
			}
		}
		if (!CollectionUtils.isEmpty(specFieldCheck)) {
			for (FieldCheck fc : specFieldCheck) {
				f = fc.fieldsCheck(mt);
				// 错误
				if (f == false) {
					addToError(m, fc);
					if (fc.getIsWarning() != true) {
						return;
					}
				}
			}
		}

		tMap = mt;
		combineRti();
	}

	// 获取内部封装的json：rti
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void combineRti() {
		if (rtiColList != null && rtiColList.size() != 0) {
			Map rti = new LinkedHashMap();
			for (String s : rtiColList) {
				rti.put(s, tMap.get(s));
			}
			rtiMaps.add(rti);
		}
	}
}
