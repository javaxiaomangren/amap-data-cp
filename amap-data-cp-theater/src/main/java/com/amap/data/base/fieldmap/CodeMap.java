package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.base.lse.LseServeUtil;
import com.amap.base.map.LonLat;
import com.amap.base.map.MapPosition;
import com.amap.base.utils.ConfigUtil;
import com.amap.base.utils.MapUtil;
import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.base.fieldmap.admap.AdcodeUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CodeMap extends FieldMap {

	protected static List<Map> cityList = new ArrayList<Map>();
	protected static List<Map> countyList = new ArrayList<Map>();
	protected static List<Map> mapList = new ArrayList<Map>();

	protected String code_map = null;
	protected List<String> xy_map = null;
	protected String addr_map = null;
	protected Map xyMap = new HashMap();
	protected static int zoomLevel;

	protected static LseServeUtil gllba = new LseServeUtil();

	static {
		zoomLevel = ConfigUtil.getInt("data_zoomlevel");

		// 市
		String sql;
		sql = "SELECT citycode code,cityname name FROM m_map_city";
		DBDataReader ddr = new DBDataReader(sql);
		cityList = ddr.readAll();

		// 区
		sql = "SELECT countycode code,countyname name FROM m_map_county";
		ddr = new DBDataReader(sql);
		countyList = ddr.readAll();

		// code与城市名对应关系
		sql = "SELECT a.countycode code ,b.cityname FROM `m_division_county` a ,`m_division_city` b"
				+ " WHERE a.citycode = b.citycode UNION "
				+ "SELECT citycode code,cityname FROM `m_division_city";
		ddr = new DBDataReader(sql);
		mapList = ddr.readAll();
	}

	public CodeMap(TempletConfig templet) {
		type = "城市code+经纬度映射";

		code_map = templet.getString("code_map");
		xy_map = templet.getList("xy_map");
		addr_map = templet.getString("addr_map");

		if (xy_map != null) {
			for (String s : xy_map) {
				String s1[] = s.split("-");
				xyMap.put(s1[0], s1[1]);
			}
		}
	}

	@Override
	public boolean fieldmap(Map from, Map to) {
		String codename = (String) from.get(code_map);
		String addr = (String) from.get(addr_map);
		Double lon = (from.get(xyMap.get("x")) == null || "".equals(from.get(xyMap.get("x"))))? null : ObjectUtil
				.toDouble(from.get(xyMap.get("x")));
		Double lat = (from.get(xyMap.get("y")) == null || "".equals(from.get(xyMap.get("y"))))? null : ObjectUtil
				.toDouble(from.get(xyMap.get("y")));

		// 有经纬度
		if (lon != null && lat != null && lon != 0 && lat != 0) {
			String code = AdcodeUtil.findCode(lon, lat);
			if (code != null) {
				// 地区编码
				to.put("code", code);

				// 经纬度和像素坐标
				MapPosition mp = MapUtil.lonLatToMapPosi(new LonLat(lon, lat),
						zoomLevel);
				to.put("x", lon);
				to.put("y", lat);
				to.put("pixelx", mp.getX());
				to.put("pixely", mp.getY());
				return true;
			} else {
				errMessage = "经纬度不能获得code";
				Map m = new HashMap();
				errValue = m;
				m.put(xyMap.get("x"), lon);
				m.put(xyMap.get("y"), lat);
				return false;
			}
		} else {

			// 无城市
			if (codename == null) {
				errMessage = "无经纬度+地区名";
				Map m = new HashMap();
				errValue = m;
				return false;
			}
			// 无地址
			if (addr == null) {
				errMessage = "无经纬度+地址";
				Map m = new HashMap();
				errValue = m;
				return false;
			}

			String code = cityListMatch(codename);
			if (code == null) {
				errMessage = "无经纬度+城市找不到代码";
				Map m = new HashMap();
				errValue = m;
				m.put(code_map, codename);
				return false;
			}

			String cityName = null;
			for (Map m : mapList) {
				String s = (String) m.get("code");
				if (code.equals(s)) {
					cityName = (String) m.get("cityname");
					break;
				}
			}
			if (cityName == null) {
				errMessage = "无经纬度+找不到对应城市名";
				Map m = new HashMap();
				errValue = m;
				m.put(code_map, codename);
				return false;
			}

			Map lonlat;
			try {
				lonlat = gllba.geoPrecisionByAddressConf(cityName,
						addr.replace("　", " "));// 中文空格要去掉
			} catch (Exception e) {
				e.printStackTrace();
				errMessage = "无经纬度+逆地理编码异常";
				Map m = new HashMap();
				errValue = m;
				m.put("cityName", cityName);
				m.put("addr", addr);
				return false;
			}

			if (lonlat == null) {
				errMessage = "无经纬度+逆地理编码空";
				Map m = new HashMap();
				errValue = m;
				m.put("cityName", cityName);
				m.put("addr", addr);
				return false;
			}

			// 地区编码
			to.put("code", code);

			// 经纬度和像素坐标
			lon = ObjectUtil.toDouble(lonlat.get("lon"));
			lat = ObjectUtil.toDouble(lonlat.get("lat"));

			MapPosition mp = MapUtil.lonLatToMapPosi(new LonLat(lon, lat),
					zoomLevel);
			to.put("x", lon);
			to.put("y", lat);
			to.put("pixelx", mp.getX());
			to.put("pixely", mp.getY());
			return true;
		}
	}

	protected String cityListMatch(String areaname) {
		String name = areaname;
		List<String> l1 = new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();

		// 市
		for (Map m : cityList) {
			if (((String) m.get("name")).equals(name)) {
				l1.add(ObjectUtil.toString(m.get("code")));
			}
		}
		if (l1.size() == 1) {
			return l1.get(0);
		}

		// 区
		for (Map m : countyList) {
			if (((String) m.get("name")).equals(name)) {
				l2.add(ObjectUtil.toString(m.get("code")));
			}
		}
		if (l2.size() == 1) {
			return l2.get(0);
		}
		return null;
	}

}
