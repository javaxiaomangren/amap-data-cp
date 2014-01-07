/**
 * 2013-10-14
 */
package roadNet;

import com.vividsolutions.jts.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roadNet.region.DivideRegion;
import roadNet.region.MaxMinLatLon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 一下子计算出全部的交叉点
 */
public class GetAllCoords {
	private static final Logger log = LoggerFactory.getLogger(ReadCsvBatch.class);
	private static MyConfigUtil recordConfig_matrix = new MyConfigUtil(
			"matrix.properties");
	
	private static String resultPath = "/road/";
	//对roads进行区域分割
	private static FindCrossCoord find = new FindCrossCoord();
	/**
	 * 读取指定路径下的所有文件
	 * @throws java.io.IOException
	 * @throws ParseException 
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void readCsvBatch(String path, String fileName) throws IOException, ParseException{
		File f = new File(path);
		File[] csvs = f.listFiles();
		for(File csv : csvs){
			String filePath = path + "/" + csv.getName();
			log.info("开始读取文件，文件名为：" + csv.getName());
			List<Map> roads = ReadFromCsv.readFromSingleCsv(filePath);
			log.info(csv.getName() + "的数据读取完成，有名称的路共有：" + roads.size() + "条，开始进行交叉点计算");
			
			String city = csv.getName().replace("R_", "").replace(".csv", "");
			if("特别行政区".equals(fileName)){
				city += fileName;
			}
			String maxmin = MaxMinLatLon.getMaxMinLatLngCity(city);
			if(maxmin == null){
				find.findCrossCoord(roads);
			} else {
				DivideRegion divideRegion = new DivideRegion(maxmin);
				String longwide = divideRegion.getRegionNum(maxmin);
				log.info("区域划分，长宽划分个数分别为：" + maxmin);
				Map region = divideRegion.divideRegion(roads);
				log.info("区域划分完成，共划分的区域块个数为：" + region.keySet().size());
				find.findCrossCoord(region, longwide);
			}
		}
	}
	
	public static void mainProcess() throws IOException, ParseException{
		String path = "/road/13Q2版全国道路面数据";
		log.info("当前路径为：" + path);
		File f = new File(path);
		File[] files = f.listFiles();
		log.info("省的个数为：" + files.length);
		int i = 0;
		for(int k = files.length - 1; k >= 0; k--){
			File file = files[k];
			String name = file.getName();
			String filePath = file.getPath();
			
			log.info("开始处理第：" + i + "个省，当前省是：" + name);
			readCsvBatch(filePath, name);
			i++;
		}
		
		String writerpath = resultPath + "allCoords.csv";
		find.writerResult(writerpath);
	}
	
	public static void main(String[] args) throws IOException, ParseException{
		try {
			mainProcess();
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
	
	public void run() throws Exception {
		String error_status = recordConfig_matrix.getString("error_status");
		if (error_status.equals("run")) {
			log.info("执行任务失败--有另一个程序在运行中");
		} else {
			log.info("执行任务成功--开始执行");
			recordConfig_matrix.setValue("error_status", "run");
			try {
				mainProcess();
			} catch (Exception e) {
				log.info(e.toString());
			}
			recordConfig_matrix.setValue("error_status", "star");
		}
	}
}
