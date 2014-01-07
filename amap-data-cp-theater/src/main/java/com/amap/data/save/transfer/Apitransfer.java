/**
 * @caoxuena
 * 2013-4-3
 *Apitransfer.java
 */
package com.amap.data.save.transfer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.data.WriteToDB;
import com.amap.data.base.FieldCheck;
import com.amap.data.base.FieldCheckUtil;
import com.amap.data.base.FieldFilter;
import com.amap.data.base.FieldFilterUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.FieldMapUtil;
import com.amap.data.base.FieldProc;
import com.amap.data.base.TempletConfig;
import com.mongodb.util.JSON;

public class Apitransfer {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
			.getLogger(Apitransfer.class);
	protected TempletConfig templet;

	protected int count;

	// 过滤
	protected List<FieldFilter> commonFieldFilter;
	// 映射处理
	protected List<FieldMap> commonFieldMap;
	// 字段检查
	protected List<FieldCheck> commonFieldCheck;

	// 特殊
	protected List<FieldFilter> specFieldFilter;
	// 映射处理
	protected List<FieldMap> specFieldMap;
	// 字段检查
	protected List<FieldCheck> specFieldCheck;

	@SuppressWarnings("rawtypes")
	protected Map fMap;
	@SuppressWarnings("rawtypes")
	protected Map tMap;
	@SuppressWarnings("rawtypes")
	protected Map error;
	protected WriteToDB writeToDB = new WriteToDB();
	protected String to_table;
	protected String to_table_error;

	private List<String> baseColList;
	private List<String> deepColList;

	private void initToTable() {
		to_table_error = templet.getString("to_table_error");
		to_table = templet.getString("to_table");
	}

	// 初始化
	public void init(TempletConfig templet) {
		this.templet = templet;
		count = 0;
		// 创建字段映射规则
		initFieldMap();
		// 定义字段
		initSeqField();
		// 初始化存储的表格名称
		initToTable();
	}

	protected void initFieldMap() {
		// 过滤
		commonFieldFilter = FieldFilterUtil.genFieldFilterList(templet);
		// 映射处理
		commonFieldMap = FieldMapUtil.genFieldMapList(templet);
		// 字段检查
		commonFieldCheck = FieldCheckUtil.genFieldCheckList(templet);

		// 过滤
		specFieldFilter = specFilter();
		// 映射处理
		specFieldMap = specMap();
		// 字段检查
		specFieldCheck = specCheck();
	}

	@SuppressWarnings("unchecked")
	protected void initSeqField() {
		baseColList = templet.getList("base_cols");
		deepColList = templet.getList("deep_cols");
	}

	// 保存原始记录

	@SuppressWarnings("rawtypes")
	public Map transfer(String str) {
		fMap = (Map) JSON.parse(str);

		// 字段转换
		transField();

		return tMap;

	}

	protected void toDB() {
		// 先判断数据信息
		if (tMap != null) {
			// 先判断表中是否已经有该条信息，有的话 直接更新，没有的话插入
			String id = tMap.get("id").toString();
			String cp = tMap.get("cp").toString();
			// 原表中已经存在该条信息，直接更新即可
			// 简化做法：先从原表中把该条信息删除，直接写入新的信息
			//更新其update_flag标记
			if (assertIsExist(id, cp)) {
				deleteFromTable(id, cp);
			}
			
			// 新增信息，直接写入
			writeToDB.toDBSingle(to_table, tMap);
		}
		// 写错误信息
		if (error != null) {
			writeToDB.toDBSingle(to_table_error, error);
		}
	}

	private void deleteFromTable(String id, String cp) {
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		String sql = "delete from " + to_table + " where id = '" + id + "' and cp = '" + cp + "'";
		dbexec.setSql(sql);
		dbexec.dbExec();
	}

	// 根据传入的id和cp判断原表中是否已经存在该条信息
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean assertIsExist(String id, String cp) {
		String sql = "select * from " + to_table + " where id = '" + id + "' and cp = '" + cp + "'";
		DBDataReader ddr = new DBDataReader(sql);
		List<Map> findResult = ddr.readList();
		if (findResult == null || findResult.size() == 0) {
			return false;
		}
		return true;
	}

	protected List<FieldFilter> specFilter() {
		return null;
	}

	protected List<FieldMap> specMap() {
		return null;
	}

	protected List<FieldCheck> specCheck() {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
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
		tMap.put("update_flag", templet.UPDATE);
		combineBaseDeep();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addToError(Map m, FieldProc fp) {
		error = new HashMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		error.put("create_time", df.format(new Date()));
		error.put("errorstep", "transfer");
		error.put("errortype", fp.getType());
		error.put("errormessage", fp.getErrMessage());
		error.put("errorvalue", fp.getErrValue());
		error.put("iswarning", fp.getIsWarning());
	}

	// 获取内部封装的json：包括base、deep和rti
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void combineBaseDeep() {
		if (baseColList != null && baseColList.size() != 0) {
			Map base = new LinkedHashMap();
			for (String s : baseColList) {
				base.put(s, tMap.get(s));
			}
			tMap.put("base", JSON.serialize(base));
		}

		if (deepColList != null && deepColList.size() != 0) {
			Map deep = new LinkedHashMap();
			for (String s : deepColList) {
				deep.put(s, tMap.get(s));
			}
			tMap.put("deep", JSON.serialize(deep));
		}
	}
}
