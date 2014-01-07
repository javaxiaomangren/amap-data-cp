package com.amap.data.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.amap.base.data.DBDataReader;
import com.amap.base.utils.ObjectUtil;

public class AreaUtil {
	private List<String> specCityeCodeList = new ArrayList<String>();// 特殊城市
	public List<String> preCityCodeList = new ArrayList<String>();// 所有城市前缀代码
	public Map<String, String> codeNameMap = new HashMap<String, String>();// 代码，对应城市名称

	@SuppressWarnings("rawtypes")
	public Map<String, List> areaMap = new HashMap<String, List>();// 城市对应地名

	private static AreaUtil instance = null;

	public static void main(String[] args) {
		// AreaDict af = AreaDict.getInstance();
		// Map<String, List> m = af.areaMap;
		// for (String key : m.keySet()) {
		// System.out.println(key + ":" + m.get(key));
		// }

		// List<String> ls = af.preCityCodeList;
		// for (String key : ls) {
		// System.out.println(key);
		// }
	}

	public String extraCityCode(String code) {
		// 前4位
		if (code.length() > 4) {
			code = code.substring(0, 4);
			if (preCityCodeList.contains(code))
				return code;
		}
		// 前2位
		if (code.length() > 2) {
			code = code.substring(0, 2);
			if (preCityCodeList.contains(code))
				return code;
		}

		return null;
	}

	public String extraCityName(String code) {
		if (code.length() < 6) {
			code = StringUtils.rightPad(code, 6, '0');
		}
		return codeNameMap.get(code);
	}

	public static synchronized AreaUtil getInstance() {
		if (instance == null) {
			instance = new AreaUtil();
		}
		return instance;
	}

	private AreaUtil() {

		// 代码，城市名映射
		loadCodeCityNameMap();

		// 特殊城市
		loadSpecCity();

		// 城市代码前缀
		loadPreCityCode();

		// 城市代码对应省市区地名
		loadAreaMap();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadCodeCityNameMap() {
		String sql = "SELECT a.countycode code ,b.cityname FROM `m_division_county` a ,`m_division_city` b"
				+ " WHERE a.citycode = b.citycode UNION "
				+ "SELECT citycode code,cityname FROM `m_division_city`";

		DBDataReader ddr = new DBDataReader();
		ddr.setSql(sql);

		List<Map> lm = ddr.readAll();
		for (Map map : lm) {
			codeNameMap.put(ObjectUtil.toString(map.get("code")),
					ObjectUtil.toString(map.get("cityname")));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadSpecCity() {
		String sql = "SELECT citycode code,cityname name FROM m_division_city WHERE flag!=5";
		DBDataReader ddr = new DBDataReader();
		ddr.setSql(sql);

		List<Map> lm = ddr.readAll();
		for (Map map : lm) {
			String code = ObjectUtil.toString(map.get("code"));
			specCityeCodeList.add(code);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadPreCityCode() {
		String sql = "SELECT citycode code,cityname name FROM m_division_city";
		DBDataReader ddr = new DBDataReader();
		ddr.setSql(sql);

		List<Map> lm = ddr.readAll();
		for (Map map : lm) {
			String code = ObjectUtil.toString(map.get("code"));

			if (!specCityeCodeList.contains(code)) {
				preCityCodeList.add(code.substring(0, 4));
			} else {
				preCityCodeList.add(code.substring(0, 2));
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void loadAreaMap() {
		// 省
		String sql = "SELECT a.citycode code,b.provname name FROM m_division_city a,m_map_prov b WHERE a.provcode=b.provcode ORDER BY citycode,provname DESC";
		mergeByCity(sql);

		// 市
		sql = "SELECT citycode code,cityname name FROM m_map_city ORDER BY citycode,cityname DESC";
		mergeByCity(sql);

		// 区
		sql = "SELECT a.citycode code,b.countyname name FROM m_division_county a,m_map_county b WHERE a.countycode=b.countycode ORDER BY a.citycode,b.countyname DESC";
		mergeByCity(sql);

		// 加中国
		for (String key : areaMap.keySet()) {
			List<String> t = areaMap.get(key);
			t.add("中国");
			t.add("中华人民共和国");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void mergeByCity(String sql) {
		DBDataReader ddr = new DBDataReader();
		ddr.setSql(sql);

		// 同代码，合并
		List<Map> lm = ddr.readAll();
		for (Map map : lm) {
			String code = ObjectUtil.toString(map.get("code"));
			String name = ObjectUtil.toString(map.get("name"));

			if (!specCityeCodeList.contains(code)) {
				code = code.substring(0, 4);
			} else {
				code = code.substring(0, 2);
			}

			List<String> l;
			if (areaMap.get(code) == null) {
				l = new ArrayList<String>();
				areaMap.put(code, l);
			} else {
				l = (List<String>) areaMap.get(code);
			}

			if (!l.contains(name))
				l.add(name);
		}
	}
}
