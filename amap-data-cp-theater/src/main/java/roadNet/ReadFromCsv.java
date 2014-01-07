/**
 * 2013-9-18
 */
package roadNet;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadFromCsv {
	private static WKTReader wkt = new WKTReader();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> readFromSingleCsv(String path) throws IOException, FileNotFoundException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"gbk"));
		String data;
		reader.readLine();
		List<Map> roads = new ArrayList<Map>();
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split(",");
			//名称为空的不考虑
			if(fields[2] == null || fields[2].equals("")){
				continue;
			}
			try{
			String id = fields[1];
			String name = fields[2];
			String coords = fields[9];
			String mesh = fields[16];
			Map road = new HashMap();
			road.put("id", id);
			road.put("name", name);
			coords = getRegularCoords(coords);
			road.put("coords", coords);
			road.put("geometry", getGeometry(coords));
			road.put("mesh", mesh);
			road.put("meshid", mesh + "_" + id);
			roads.add(road);
			}catch (Exception e) {
			}
		}
		return roads;
	}
	
	/**
	 * 直接把坐标读成Geometry
	 * @throws ParseException 
	 */
	private static Geometry getGeometry(String coords) throws ParseException{
		return wkt.read(coords);
	}
	
	/**
	 * 处理坐标成标准格式:先取3位小数，再除以3600
	 */
	private static String getRegularCoords(String coords){
		String[] fields = coords.split(" ");
		String newCoords = "";
		for(String field : fields){
			String[] xys = field.split(";");
			//取出x坐标的开头：都是两位数
			String x = Double.parseDouble(xys[0]) / 1000 / 3600 + "";
			String y = Double.parseDouble(xys[1]) / 1000 / 3600 + "";
			
//			String x = xys[0];
//			String y = xys[1];
			
			//LINESTRING (0 0, 10 10, 20 20)
			if(newCoords == ""){
				newCoords += x + " " + y;
			} else {
				newCoords += ", " + x + " " + y;
			}
		}
		newCoords = "LINESTRING (" + newCoords + ")";
		return newCoords;
	}
}
