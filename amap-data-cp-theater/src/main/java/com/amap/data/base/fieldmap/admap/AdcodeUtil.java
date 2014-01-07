package com.amap.data.base.fieldmap.admap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.data.DBDataReader;
import com.amap.base.map.LonLat;
import com.amap.base.map.MapTile;
import com.amap.base.utils.MapUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@SuppressWarnings("unchecked")
public class AdcodeUtil {
	
private static int zoomLevel = 12;

	@SuppressWarnings("rawtypes")
	private static List<Map> citygeo;
	
	private static Map<String, List<String>> cityMap;
	
	static {
		// read info from m_division_city
		String sql;
		sql = "SELECT citycode,ASTEXT(geom) geomtext FROM m_city_geom";
		DBDataReader cityRd = new DBDataReader(sql);
		citygeo = cityRd.readAll();
		
		//read info from m_city_keycode
		cityMap = new HashMap<String, List<String>>();
		sql = "select xyindex, code from m_city_keycode";
		DBDataReader codeRd = new DBDataReader(sql);
		@SuppressWarnings("rawtypes")
		List<Map> keyCodeInfo = codeRd.readAll();
		for(int i = 0; i < keyCodeInfo.size(); i++){
			String key = keyCodeInfo.get(i).get("xyindex").toString();
			String codeList = keyCodeInfo.get(i).get("code").toString();
			List<String> code = new ArrayList<String>();
			
			if(codeList.contains(",")){
				String[] fields = codeList.split(",");
				for(int j = 0; j < fields.length; j++){
					code.add(j, fields[j].trim());
				}
			}else{
				code.add(0, codeList);
			}
			
			cityMap.put(key, code);
		}
		
	}
	
	
	// 根据城市，返回城市编码，错误为null值
		@SuppressWarnings({ "rawtypes", "unused" })
		public static String findCode(double lon, double lat) {

			// step 1: find the x,y index of input lon and lat
			LonLat lonlat = new LonLat(lon, lat);
			MapTile p;
			p = MapUtil.lonLatToMapTile(lonlat, zoomLevel);

			// step 2: find the code number of p
			String key = p.getX() + "_" + p.getY();

			List<String> l = null;
			l = cityMap.get(key);
			
			if(l == null){
				return null;
			}
			
			String temp = l.toString();
			temp = temp.replace("[", "");
			temp = temp.replace("]", "");

			String[] codes = temp.split(",");
			
			// step 3: assert the number of code
			if (codes.length == 1) {
				return codes[0];
			} else if (codes.length >= 2) {
				// select the right code
				// get the geometry of point
				Coordinate coordinate = new Coordinate(lon, lat);
				Geometry poi = new GeometryFactory().createPoint(coordinate);

				// loop all the code of current xyIndex
				for (int i = 0; i < codes.length; i++) {
					String code = codes[i].trim();

					Object o = (Object) code;

					// get the geom from code
					String geom = null;
					for (Map city : citygeo) {
						if (city.get("citycode").toString().equals(code)) {
							geom = city.get("geomtext").toString();
						}
					}

					WKTReader wkt = new WKTReader();
					boolean flag = false;
					try {
						if (geom.startsWith("P")) {
							Polygon polygon;
							polygon = (Polygon) wkt.read(geom);

							flag = polygon.contains(poi) || polygon.intersects(poi);
						} else {
							MultiPolygon mp = (MultiPolygon) wkt.read(geom);
							flag = mp.contains(poi) || mp.intersects(poi);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (flag == true) {
						return code;
					}
				}

				return null;
			} else {
				return null;
			}
		}

		public static void main(String[] args) throws ParseException {
			//{ycoord=42.873440755208335, xcoord=97.43083089192709}
			double lon = 149.113008;
			double lat = 46.1465568;
			//System.out.println(System.currentTimeMillis());
			String code = findCode(lon, lat);
			System.out.println(code);
			//System.out.println(System.currentTimeMillis());
		}

}
