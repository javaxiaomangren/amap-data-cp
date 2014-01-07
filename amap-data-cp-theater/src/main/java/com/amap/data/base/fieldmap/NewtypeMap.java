package com.amap.data.base.fieldmap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.base.data.DBDataReader;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

//caoxuena：函数功能为根据指定字段从m_map_crawl_newtype中找到对应的new_type
public class NewtypeMap extends FieldMap {
	private String srctype;
	private String srctag;

	@SuppressWarnings("rawtypes")
	public NewtypeMap(TempletConfig templet) {
		type = "类型newtype_map映射";

		srctype = templet.getString("srctype");
		srctag = templet.getString("newtype_map");
		Map m = new HashMap();
		errValue = m;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		// 如果srctag值为static，即类型固定为一个值
		// 为该值的类型有：anjuke sinahouse dianping_gouwu university upeng
		if (srctag.equalsIgnoreCase("static")) {
			String sql = "SELECT new_type FROM m_map_crawl_newtype WHERE srctype = '"
					+ srctype + "'";
			DBDataReader ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			Map typeMap = new HashMap();
			if (ddr.readAll().size() == 1) {

				typeMap = (Map) ddr.readSingle();
				String new_type = typeMap.get("new_type").toString();
				to.put("new_type", new_type);
				return true;
			} else {
				errMessage = srctype + "类别映射";
				errValue.put(srctag, from.get(srctag));
				return false;
			}
		} else {
			// 根据name获得new_type的有xiaomishu dianping mall damai clubzone
			if (srctag.equalsIgnoreCase("name")
					|| srctype.contains("xiaomishu")
					|| srctype.contains("dianping") || srctype.contains("mall")
					|| srctype.contains("damai")
					|| srctype.contains("clubzone")) {
				// 获得名称name
				String name = (String) from.get(srctag);
				String temp = srctype.replace("cp", "crawl");
				String sql = "SELECT new_type FROM m_map_crawl_newtype WHERE srctype = '"
						+ temp + "' and srctag = '" + name + "'";
				if (name == null) {
					sql = "SELECT new_type FROM m_map_crawl_newtype WHERE srctype = '"
							+ temp + "' and srctag IS NULL";
				}
				DBDataReader ddr = new DBDataReader(sql);
				ddr.setDbenv(null);
				Map typeMap = new HashMap();
				if (ddr.readAll().size() == 1) {

					typeMap = (Map) ddr.readSingle();
					String new_type = typeMap.get("new_type").toString();
					to.put("new_type", new_type);
					return true;
				} else {
					errMessage = srctype + "类别映射";
					errValue.put(srctag, name);
					return false;
				}
			} else if (srctype.contains("building")) {// 写字楼、大厦
				String tag = (String) from.get(srctag);
				if (tag != null) {
					String sql = "SELECT new_type FROM m_map_crawl_newtype WHERE srctype = '"
							+ srctype + "' and srctag = '" + tag + "'";
					DBDataReader ddr = new DBDataReader(sql);
					ddr.setDbenv(null);
					Map typeMap = new HashMap();
					if (ddr.readAll().size() == 1) {

						typeMap = (Map) ddr.readSingle();
						String new_type = typeMap.get("new_type").toString();
						to.put("new_type", new_type);
						return true;
					} else {
						to.put("new_type", "120000");
					}
				} else {
					errMessage = srctype + "类别映射";
					errValue.put(srctag, from.get(srctag));
					return false;
				}
			} else {// 宾馆酒店类
					// 从给定的字段中抽取数字部分hotel_stars05
				String tag = (String) from.get(srctag);
				String star = "1";// 默认为1星

				// 通过正则匹配找到给定的酒店星级
				if (tag != null) {
					String regEx = "(\\d{1,})";
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(tag);
					while (m.find()) {
						star = m.group();
					}
				}
				star = star.replace("0", "");

				// 根据来源和星级找到其对应的new_type
				String sql = "SELECT new_type FROM m_map_crawl_newtype WHERE srctype = '"
						+ srctype + "' and srctag = '" + star + "'";
				DBDataReader ddr = new DBDataReader(sql);
				ddr.setDbenv(null);
				Map typeMap = new HashMap();
				if (ddr.readAll().size() == 1) {

					typeMap = (Map) ddr.readSingle();
					String new_type = typeMap.get("new_type").toString();
					to.put("new_type", new_type);
					return true;
				} else {
					errMessage = srctype + "类别映射";
					errValue.put(srctag, from.get(srctag));
					return false;
				}
			}// end 宾馆酒店
		}
		return true;
	}
}
