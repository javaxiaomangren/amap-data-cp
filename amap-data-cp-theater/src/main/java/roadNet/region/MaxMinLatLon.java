/**
 * 2013-10-9
 */
package roadNet.region;

import com.amap.base.data.DBDataReader;

import java.util.List;
import java.util.Map;

/**
 * 找到对应城市的最大最小坐标；
 * 返回结果是：maxLon + ";" + maxLat + ";" + minLon + ";" + minLat
 */
public class MaxMinLatLon {
	/**
	 * 根据传入的城市名，找到其最大最小经纬度；如果表中没有找到该城市，则返回null
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getMaxMinLatLngCity(String city){
		
		String sql = "SELECT ASTEXT(geom) geomtext FROM m_city_geom where cityname = '" + city + "'";

		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> coords = ddr.readAll();
		
		if(coords == null || coords.size() == 0){
			return null;
		} 
		
		return findMaxMincoords(coords.get(0).get("geomtext").toString());
	}
	
	/**
	 * 找到最大做小坐标
	 */
	private static String findMaxMincoords(String geom){
		double maxLon = 0.0;
		double maxLat = 0.0;

		double minLon = 180.0;
		double minLat = 90.0;
		
		if (geom.startsWith("P")) {
			geom = geom.replace("POLYGON((", "");
			geom = geom.replace("))", "");
			geom = geom.replace(")", "");
			geom = geom.replace("(", "");

			String[] fields = geom.split(",");

			for (int j = 0; j < fields.length; j++) {
				double lon = Double.parseDouble((fields[j].split(" "))[0]);
				double lat = Double.parseDouble((fields[j].split(" "))[1]);

				// the max lon
				if (lon > maxLon) {
					maxLon = lon;
				}

				// the max lat
				if (lat > maxLat) {
					maxLat = lat;
				}

				// the min lon
				if (lon < minLon) {
					minLon = lon;
				}

				// the min lat
				if (lat < minLat) {
					minLat = lat;
				}
			}
		} else {

			geom = geom.replace("MULTIPOLYGON(((", "");
			geom = geom.replace(")))", "");
			geom = geom.replace("))", "");
			geom = geom.replace("(((", "");
			geom = geom.replace("((", "");
			geom = geom.replace(")", "");
			geom = geom.replace("(", "");
			

			String[] fields = geom.split(",");

			for (int j = 0; j < fields.length; j++) {
				double lon = Double.parseDouble((fields[j].split(" "))[0]);
				double lat = Double.parseDouble((fields[j].split(" "))[1]);

				// the max lon
				if (lon > maxLon) {
					maxLon = lon;
				}

				// the max lat
				if (lat > maxLat) {
					maxLat = lat;
				}

				// the min lon
				if (lon < minLon) {
					minLon = lon;
				}

				// the min lat
				if (lat < minLat) {
					minLat = lat;
				}
			}
		}
		
		return maxLon + ";" + maxLat + ";" + minLon + ";" + minLat;
	}
	
	/**
	 * 传入参数为处理后的标准道路坐标，返回为最大最小经纬度
	 */
	public static String getMaxMinLatLngRoad(String road){
		double maxLon = 0.0;
		double maxLat = 0.0;

		double minLon = 180.0;
		double minLat = 90.0;
		
		//LINESTRING (0 0, 10 10, 20 20)
		road = road.replace("LINESTRING (", "").replace(")", "");
		String[] fields = road.split(", ");
		for(String field : fields){
			String[] xy = field.split(" ");
			double lon = Double.parseDouble(xy[0]);
			double lat = Double.parseDouble(xy[1]);
			
			// the max lon
			if (lon > maxLon) {
				maxLon = lon;
			}

			// the max lat
			if (lat > maxLat) {
				maxLat = lat;
			}

			// the min lon
			if (lon < minLon) {
				minLon = lon;
			}

			// the min lat
			if (lat < minLat) {
				minLat = lat;
			}
		}
		
		return maxLon + ";" + maxLat + ";" + minLon + ";" + minLat;
	}
	
	public static void main(String[] args){
		String city = "重庆市";
		
		String result = getMaxMinLatLngCity(city);
		
		System.out.println(result);
	}
}
