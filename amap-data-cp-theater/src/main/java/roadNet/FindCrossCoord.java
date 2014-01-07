/**
 * 2013-9-18
 */
package roadNet;

import com.vividsolutions.jts.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roadNet.region.DivideRegion;
import roadNet.region.MaxMinLatLon;
import zengkunceju.ProjectionUtil;

import java.io.*;
import java.util.*;

public class FindCrossCoord {
	private final Logger log = LoggerFactory.getLogger(FindCrossCoord.class);
	
	// 距离在一定范围内的定义为重复交叉点
	private int maxDistance = 100;
	private ProjectionUtil pUtil = new ProjectionUtil();
	
	//用于判断是否包含此节点
	private Set<String> crossNames;
	@SuppressWarnings("rawtypes")
	//结果（名称——坐标）
	private Map crossCoords;
	
	@SuppressWarnings("rawtypes")
	public FindCrossCoord(){
		crossNames = new HashSet<String>();
		crossCoords = new HashMap();
	}
	
	@SuppressWarnings("rawtypes")
	public void Testsingle() throws FileNotFoundException,
			IOException, ParseException {
		String path = "E:/road.csv";
		List<Map> roads = ReadFromCsv.readFromSingleCsv(path);
		log.info("当前城市有名称的路径共有：" + roads.size());
		
		String city = "北京市";
		//对roads进行区域分割
		String maxmin = MaxMinLatLon.getMaxMinLatLngCity(city);
		log.info("区域划分，最大最小经纬度分别为：" + maxmin);
		if(maxmin == null){
			findCrossCoord(roads);
		} else {
			DivideRegion divideRegion = new DivideRegion(maxmin);
			String longwide = divideRegion.getRegionNum(maxmin);
			Map region = divideRegion.divideRegion(roads);
			log.info("区域划分完成，共划分的区域块个数为：" + region.keySet().size());
			findCrossCoord(region, longwide);
		}
		
		writerResult("E:/cross.csv");
	}
	
	/**
	 * 区域分割计算，快速计算
	 * @throws ParseException 
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.UnsupportedEncodingException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void findCrossCoord(Map region, String longwide) throws ParseException, UnsupportedEncodingException, FileNotFoundException{
		String[] fields = longwide.split("_");
		int longNum = Integer.parseInt(fields[0]);
		int wideNum = Integer.parseInt(fields[1]);
		for(int i = 0; i <= longNum; i++){
			if(i != 0 && i % 100 == 0){
				log.info("经度共划分的个数为：" + longNum + "; 当前已经处理：" + i);
			}
			for(int j = 0; j <= wideNum; j++){
				List<Map> roads = new ArrayList<Map>();
				try{
					roads = (List<Map>) region.get(i + "_" + j);
				}catch (Exception e) {
				}
				if(roads != null && roads.size() > 1){
					findCrossCoordCore(roads);
				}
			}
		}
	}

	/**
	 * sql中没有该城市的坐标信息，无法进行区域分割，采用最原始的方式计算
	 */
	@SuppressWarnings("rawtypes")
	public void findCrossCoord(List<Map> roads)
			throws IOException, ParseException {
		// 找节点
		findCrossCoordCore(roads);
		log.info("交叉点计算完成，当前市的交叉点总个数为：" + crossCoords.size());
	}

	/**
	 * 输出结果
	 * @throws java.io.IOException
	 */
	public void writerResult(String writerpath) throws IOException {
		File file = new File(writerpath);
		file.mkdirs();
		file.delete();
		File f0 = new File(writerpath);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		log.info("交叉点计算完成，当前市的交叉点总个数为：" + crossCoords.size());
		// 输出结果
		writer.write("名称,交叉点坐标");
		writer.write("\n");
		for (Object key : crossCoords.keySet()) {
			writer.write(key + "," + crossCoords.get(key));
			writer.write("\n");
		}
		writer.close();
	}

	/**
	 * 根据传入的roads信息，找到对应的所有节点
	 * @throws ParseException 
	 */
	@SuppressWarnings("rawtypes")
	private void findCrossCoordCore(List<Map> roads) throws ParseException{
		for (int i = 0; i < roads.size() - 1; i++) {
			for (int j = i + 1; j < roads.size(); j++) {
				if (i == j) {
					continue;
				}
				if (roads.get(i).get("name").equals(roads.get(j).get("name"))) {
					continue;
				}
				Map coord = CombineRoad
						.getRoadCross(roads.get(i), roads.get(j));
				if (coord != null && !coord.equals("")) {
					// 判断对应的mesh、id和名字下是否已经存在交叉点，如果是，直接把交叉坐标加进去即可；否则整体添加
					crossCoords = assertContains(crossCoords, coord);
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map assertContains(Map crossCoords, Map coord) {
		Object name = coord.get("name");
		Object coords = coord.get("coords");

		//如果该交叉点之前已经出现
		if (crossNames.contains(name)){
			//取出原来的交叉点坐标信息
			Object oldcoords = crossCoords.get(name);
			//如果新交叉点坐标不在之前计算出来的交叉点中
			if (!oldcoords.toString().contains(coords.toString())){
				Map old = new HashMap();
				old.put("coords", oldcoords);
				String newcoord = assertNewCoords(old, coord);
				//用新的交叉点信息替换旧的
				crossCoords.put(name, newcoord);
			}
		} else {
			//该交叉点是新计算出来的，直接添加对应的交叉点名称
			crossNames.add(name.toString());
			
			//把对应的交叉点信息添加到结果集合中
			crossCoords.put(name, coords);
		}
		return crossCoords;
	}

	/**
	 * 判断距离是否在指定范围内,如果不在指定范围内则在原来交叉点坐标的基础上补充新增的交叉点
	 */
	@SuppressWarnings({ "rawtypes" })
	private String assertNewCoords(Map crossCoord, Map coord) {
		String xy = coord.get("coords").toString();
		String[] coordXys = xy.split(";");
		String oldXy = crossCoord.get("coords").toString();
		String[] oldXys = oldXy.split(";");
		
		//用于存储交叉点坐标集，先用原来的结果初始化，判断出来有新增的，则直接加上即可
		String coords = oldXy;

		for (int i = 0; i < coordXys.length; i++) {
			boolean flag = true;
			for (int j = 0; j < oldXys.length; j++) {
				double x = Double.parseDouble(coordXys[i].split(" ")[0]);
				double y = Double.parseDouble(coordXys[i].split(" ")[1]);

				double oldX = 0.0;
				double oldY = 0.0;
				try {
					oldX = Double.parseDouble(oldXys[j].split(" ")[0]);
					oldY = Double.parseDouble(oldXys[j].split(" ")[1]);
				} catch (Exception e) {
					System.out.println();
				}

				int distance = pUtil.ComputeFormCD(oldY, oldX, y, x);
				if (distance < maxDistance) {
					flag = false;
				}
			}

			if (flag) {
				if (coords.equals("")) {
					coords = coordXys[i];
				} else {
					coords += ";" + coordXys[i];
				}
			}
		}

		if (coords.equals("")) {
			return null;
		}
		return coords;
	}
}
