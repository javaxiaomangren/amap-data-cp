/**
 * 2013-12-18
 */
package mining;

import com.amap.base.data.DBDataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * ID继承机制，从table中根据cp和poiid选出之前的id继续使用，保证统一poiid使用的是一个id
 */
public class GetIdFromTable {
	private static String table = "poi_deep";
	private static final Logger log = LoggerFactory.getLogger(GetIdFromTable.class);
	/**
	 * 如果表中已经对应的数据，则取出之前的id，否则返回表中最大的id号加1
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getIdfromTable(String cp, Object poiid){
		String sql = "select * from " + table + " where cp = '" + cp + "' and poiid = '" + poiid + "'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> datas = ddr.readAll();
		if (datas == null || datas.size() == 0){
			sql = "SELECT MAX(CAST(id AS SIGNED)) FROM " + table + " where cp = '" + cp + "'";
			ddr = new DBDataReader(sql);
			ddr.setDbenv(null);
			List<Map> results = ddr.readAll(); 
			if(results == null || results.get(0).get("MAX(CAST(id AS SIGNED))") == null || results.get(0).get("MAX(CAST(id AS SIGNED))").equals("null")){
				return 0;
			} else {
				Object id = results.get(0).get("MAX(CAST(id AS SIGNED))");
				return Integer.parseInt(id.toString()) + 1;
			}
		} else if (datas.size() == 1){
			return datas.get(0).get("id");
		} else {
			log.info("错误：有两个相同的poiid");
		}
		
		return null;
	}
}
