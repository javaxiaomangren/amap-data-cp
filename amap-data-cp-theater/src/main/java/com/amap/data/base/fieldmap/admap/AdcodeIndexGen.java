package com.amap.data.base.fieldmap.admap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.base.map.LonLat;
import com.amap.base.map.MapTile;
import com.amap.base.utils.MapUtil;
import com.amap.base.utils.ObjectUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdcodeIndexGen {

	private static int zoomLevel = 12;

	private static List<Map> citygeo;

	private static int MaxX, MinX, MaxY, MinY;

	private static Map<String, List<String>> cityMap;

	static {
		// read info from m_division_city
		String sql;
		sql = "SELECT citycode,ASTEXT(geom) geomtext FROM m_city_geom";
		DBDataReader cityRd = new DBDataReader(sql);
		citygeo = cityRd.readAll();

		// 每次调用程序只做一次
		cityMap = new HashMap();
		// loop city

		for (Map city : citygeo) {
			// get the geom
			String geom = ObjectUtil.toString(city.get("geomtext"));

			if (geom.startsWith("P")) {
				dealCitymap(geom, city);
			} else {
				String temp = geom.replace(")),", "))。");
				temp = temp.replace(")))", "))。");
				String[] geoms = temp.split("。");
				for (int i = 0; i < geoms.length; i++) {
					dealCitymap(geoms[i], city);
				}
			}
		}
	}

	public static void dealCitymap(String geom, Map city) {
		// step 1: find the max,min lon and lat
		double maxLon, minLon, maxLat, minLat;
		String temp = findMaxMinLonLat(geom);
		String[] fields = temp.split(",");
		maxLon = Double.parseDouble(fields[0]);
		minLon = Double.parseDouble(fields[1]);
		maxLat = Double.parseDouble(fields[2]);
		minLat = Double.parseDouble(fields[3]);

		// step 2:find the index according to the max,min lat and lon
		temp = findMaxMinIndex(maxLon, minLon, maxLat, minLat);
		fields = temp.split(",");
		MaxX = Integer.parseInt(fields[0]);
		MinX = Integer.parseInt(fields[1]);
		MaxY = Integer.parseInt(fields[2]);
		MinY = Integer.parseInt(fields[3]);

		// step 3: assert the relationship
		Polygon polygon = null;
		WKTReader wkt = new WKTReader();

		if (geom.startsWith("M")) {
			geom = geom.replace("MULTIPOLYGON(", "POLYGON");
		} else if (geom.startsWith("((")) {
			geom = "POLYGON" + geom;
		} else {
			// do nothing
		}

		try {
			polygon = (Polygon) wkt.read(geom);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		for (int i = MinX; i <= MaxX; i++) {
			for (int j = MinY; j <= MaxY; j++) {
				MapTile mapTile = new MapTile(i, j, zoomLevel);
				// get the left up lon and lat of current mapTile
				LonLat lonlatLeftUp = MapUtil.mapTileLeftUpLonLat(mapTile);
				// get the right down lon and lat of current mapTile
				LonLat lonlatRightDown = MapUtil
						.mapTileRightDownLonLat(mapTile);

				// get the geometry of current mapTile
				Coordinate[] coordinates = new Coordinate[] {
						new Coordinate(lonlatLeftUp.getLongitude(),
								lonlatLeftUp.getLatitude()),
						new Coordinate(lonlatRightDown.getLongitude(),
								lonlatLeftUp.getLatitude()),
						new Coordinate(lonlatRightDown.getLongitude(),
								lonlatRightDown.getLatitude()),
						new Coordinate(lonlatLeftUp.getLongitude(),
								lonlatRightDown.getLatitude()),
						new Coordinate(lonlatLeftUp.getLongitude(),
								lonlatLeftUp.getLatitude()) };
				Geometry g = new GeometryFactory()
						.createLineString(coordinates);

				boolean flag = polygon.contains(g) || polygon.intersects(g);
				if (true == flag) {

					String key = i + "_" + j;

					String code = ObjectUtil.toString(city.get("citycode"));

					List l = null;
					if (cityMap.keySet().contains(key)) {
						l = (List) cityMap.get(key);
					} else {
						l = new ArrayList();
					}

					l.add(code);
					cityMap.put(key, l);
				}
			}
		}
	}

	// get the max,min lon and lat String from geom
	public static String findMaxMinLonLat(String geom) {
		geom = geom.replace("MULTIPOLYGON(((", "");
		geom = geom.replace("POLYGON((", "");
		geom = geom.replace("((", "");
		geom = geom.replace("))", "");
		geom = geom.replace(")", "");
		geom = geom.replace("(", "");

		double maxLon = -180.0;
		double minLon = 180.0;
		double maxLat = -90.0;
		double minLat = 90.0;

		String[] fields = geom.split(",");
		for (int i = 0; i < fields.length; i++) {
			String lonLat = fields[i];
			int index = lonLat.indexOf(" ");
			double lon = Double.parseDouble(lonLat.substring(0, index));
			double lat = Double.parseDouble(lonLat.substring(index + 1));

			// record the max lon
			if (maxLon < lon) {
				maxLon = lon;
			}

			// record the min lon
			if (minLon > lon) {
				minLon = lon;
			}

			// record the max lat
			if (maxLat < lat) {
				maxLat = lat;
			}

			// record the min lat
			if (minLat > lat) {
				minLat = lat;
			}
		}

		String str;
		str = maxLon + "," + minLon + "," + maxLat + "," + minLat;

		return str;
	}

	// get the max,min index of x and y
	public static String findMaxMinIndex(double maxLon, double minLon,
			double maxLat, double minLat) {
		// left up
		LonLat lonlat = new LonLat(minLon, maxLat);
		MapTile leftUp;
		leftUp = MapUtil.lonLatToMapTile(lonlat, zoomLevel);

		// right down
		lonlat = new LonLat(maxLon, minLat);
		MapTile rightDown;
		rightDown = MapUtil.lonLatToMapTile(lonlat, zoomLevel);

		MaxX = rightDown.getX();
		MinX = leftUp.getX();
		MaxY = rightDown.getY();
		MinY = leftUp.getY();

		String str;
		str = MaxX + "," + MinX + "," + MaxY + "," + MinY;

		return str;
	}

	public static void main(String[] args) throws ParseException {

		List<String> sqlList = new ArrayList<String>();
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);

		/*
		 * for(int i = minX; i < maxX; i++){ for(int j = minY; j < maxY; j++){
		 * String key = i + "_" + j; List l = cityMap.get(key);
		 * 
		 * if(l == null){ continue; }else{ String code = l.toString();
		 * 
		 * sql = "insert into table key_code (key, code) values ('" + key +
		 * "', '" + code + "')"; sqlList.add(sql); } } }
		 */

		// use key set
		Set set = cityMap.keySet();
		Iterator iterator = set.iterator();
		String key = null;
		String sql;
		while (iterator.hasNext()) {
			key = (String) iterator.next();

			List l = cityMap.get(key);
			
			String code = l.toString();
			code = code.replace("[", "");
			code = code.replace("]", "");

			sql = "insert into m_city_keycode values ('" + key
					+ "', '" + code + "')";
			sqlList.add(sql);

		}

		dbexec.setSqlList(sqlList);
		dbexec.dbExec();
	}
}
