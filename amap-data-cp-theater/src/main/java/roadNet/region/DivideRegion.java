/**
 * 2013-10-9
 */
package roadNet.region;

import zengkunceju.ProjectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DivideRegion {
	private int regionLength = 100;//unit:m
	
	private double maxLon;
	private double maxLat;

	private double minLon;
	private double minLat;
	
	//计算当前城市总共可以划分几个区域：横向多少和纵向多少，然后评分经纬度，看具体跨多少经度和多少纬度
	private double LonLength;
	private double LatLength;
	
	private int longNum;
	private int wideNum;
	
	/**
	 * 确定一个城市的最大最小经纬度
	 */
	public DivideRegion(String result){
		//maxLon + ";" + maxLat + ";" + minLon + ";" + minLat
		String[] fields = result.split(";");
		maxLon = Double.parseDouble(fields[0]);
		maxLat = Double.parseDouble(fields[1]);
		minLon = Double.parseDouble(fields[2]);
		minLat = Double.parseDouble(fields[3]);
		
		//计算横向的距离
		ProjectionUtil pUtil = new ProjectionUtil();
		int distance = pUtil.ComputeFormCD(maxLon, maxLat, minLon, maxLat);
		longNum = distance / regionLength + 1;
		LonLength = (maxLon - minLon) / longNum;
		
		//计算纵向的距离
		distance = pUtil.ComputeFormCD(maxLon, maxLat, maxLon, minLat);
		wideNum = distance / regionLength + 1;
		LatLength = (maxLat - minLat) / wideNum;
	}
	
	/** 
	 * 确定当前城市的最大方格个数:长_宽
	 */
	public String getRegionNum(String result){
		return longNum + "_" + wideNum;
	}
	
	/**
	 * 将当前城市的所有数据划分到不同的方格中
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map divideRegion(List<Map> roads){
		Map result = new HashMap();
		for(Map road : roads){
			String maxminLonLat = MaxMinLatLon.getMaxMinLatLngRoad(road.get("coords").toString());
			String[] fields = maxminLonLat.split(";");
			double Lonmax = Double.parseDouble(fields[0]);
			double Latmax = Double.parseDouble(fields[1]);
			double Lonmin = Double.parseDouble(fields[2]);
			double Latmin = Double.parseDouble(fields[3]);
			
			int minLong = Math.abs((int)((Lonmin - minLon) / LonLength) - 1);
			int maxLong = Math.abs((int)((Lonmax - minLon) / LonLength) + 1);
			
			int minWide = Math.abs((int)((Latmin - minLat) / LatLength) - 1);
			int maxWide = Math.abs((int)((Latmax - minLat) / LatLength) + 1);
			
			for(int i = minLong; i <= maxLong; i++){
				for (int j = minWide; j <= maxWide; j++){
					//结果集合中已经有该方格的道路信息
					if(result != null && result.containsKey(i + "_" + j)){
						List<Map> temp = (List<Map>) result.get(i + "_" + j);
						temp.add(road);
						result.put(i + "_" + j, temp);
					} else {
						List<Map> temp = new ArrayList<Map>();
						temp.add(road);
						result.put(i + "_" + j, temp);
					}
				}
			}
		}
		return result;
	}
}
