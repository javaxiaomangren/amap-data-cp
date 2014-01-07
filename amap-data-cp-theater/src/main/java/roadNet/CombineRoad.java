/**
 * 2013-9-22
 */
package roadNet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import java.util.HashMap;
import java.util.Map;

public class CombineRoad {
	/**
	 * 找到两条路的交叉点
	 * @throws ParseException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getRoadCross(Map a, Map b) throws ParseException{
		Map coord = new HashMap();
		Geometry aRoad = (Geometry) a.get("geometry");
		Geometry bRoad = (Geometry) b.get("geometry");
		Geometry g = aRoad.intersection(bRoad);
		if(g != null && !g.isEmpty()){
			Coordinate[] xys = g.getCoordinates();
			String coordinate = "";
			for(Coordinate xy : xys){
				if(coordinate.equals("")){
					coordinate = xy.x + " " + xy.y;
				} else {
					coordinate += ";" + xy.x + " " + xy.y;
				}
			}
			coord.put("coords", coordinate);
			String aName = a.get("name").toString();
			String bName = b.get("name").toString();
			if(aName.compareToIgnoreCase(bName) < 0){
				coord.put("name", aName + "_" + bName);
			} else {
				coord.put("name", bName + "_" + aName);
			}
			return coord;
		} //else if(aRoad.isWithinDistance(bRoad, maxDistance)){
		return null;
	}
	
	/**
	 * Test
	 * @throws ParseException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws ParseException{
		Map a = new HashMap();
		a.put("coords", "LINESTRING (0 0, 10 10, 20 20)");
		
		Map b = new HashMap();
		b.put("coords", "LINESTRING (0 15, 6 15, 20 15)");
		
		getRoadCross(a, b);
	}
}
