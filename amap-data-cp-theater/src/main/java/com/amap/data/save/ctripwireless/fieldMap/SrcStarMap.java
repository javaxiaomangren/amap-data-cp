/**
 * 2013-6-6
 */
package com.amap.data.save.ctripwireless.fieldMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class SrcStarMap extends FieldMap {
	private final Logger log = LoggerFactory.getLogger(SrcStarMap.class);
	private String src_star_map;
	public SrcStarMap(TempletConfig templet) {
		type = "深度src_star映射";
		src_star_map = templet.getString("src_star_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		Map deep = (Map) from.get("deep");
		Object score_total = deep.get(src_star_map);
		String src_star = null;
		try{
			if(score_total != null && !score_total.equals("")){
				src_star = getNewRating(score_total.toString());
			}
		}catch (Exception e) {
			log.info("src_star转换错误，score_total的值是：" + score_total);
		}
		
		 to.put("src_star", src_star);
		return true;
	}
	
	public static String getNewRating(String oldrating) {
		String newvalue = oldrating;
		try {
			if(oldrating.indexOf(".")>=0) {
				String[] array = oldrating.split("\\.");
				
				if(array.length>=2) {
					int val1 = ObjectUtil.toInteger(array[0]);
					String val2str = ObjectUtil.toString(array[1]);
					int val2 = 0;
					if(val2str.length()>1) {
						System.out.println("val2str.substring(0, 1)="+val2str.substring(0, 1));
						val2 = Integer.parseInt(val2str.substring(0, 1));
					}else {
						val2 = Integer.parseInt(val2str);
					}
					if(val2==0||val2==1||val2==2||val2==3) {
						val2 = 0;
					}else if(val2==4||val2==5||val2==6) {
						val2 = 5;
					}else if(val2==7||val2==8||val2==9) {
						val1 +=1; 
						val2 = 0;
					}
					newvalue = val1+"."+val2;
				}
			}
		} catch (Exception e) {
			System.out.println(oldrating);
			e.printStackTrace();
		}
		return newvalue;
	}
}
