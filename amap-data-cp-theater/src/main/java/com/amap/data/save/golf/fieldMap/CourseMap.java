/**
 * 2013-10-22
 */
package com.amap.data.save.golf.fieldMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;

public class CourseMap  extends FieldMap {
	private List<String> keys;
	
	@SuppressWarnings("unchecked")
	public CourseMap(TempletConfig templet) {
		type = "球场信息映射";
		keys = templet.getList("course_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> courses = new ArrayList<LinkedHashMap>();
		if(from.get("course") != null){
			List<Map> temps = (List<Map>) from.get("course");
			
			for(Map temp : temps){
				temp = SaveHelper.transferToSmall(temp);
				LinkedHashMap course = new LinkedHashMap();
				
				for(String key : keys){
					if("pic_info".equals(key)){
						//temps中的图片信息映射
						course.put("pic_info", getPics(temp));
					} else {
						course.put(key, temp.get(key) == null || temp.get(key).equals("") ? null : temp.get(key));
					}
				}
				courses.add(course);
			}
		}
		 to.put("course", courses);
		return true;
	}
	
	//图片处理
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<LinkedHashMap> getPics(Map info){
		List<LinkedHashMap> results = new ArrayList<LinkedHashMap>();
		if(info.get("pic_info") != null){
			List<Map> pics = (List<Map>) info.get("pic_info");
			for(Map pic : pics){
				if(pic.get("URL") == null || pic.get("URL").equals("")){
					continue;
				}
				LinkedHashMap temp = new LinkedHashMap();
				if (pic.get("DEFAULT") != null && pic.get("DEFAULT").equals("1")){
					temp.put("iscover", "1");
				} else {
					temp.put("iscover", "0");
				}
				temp.put("src_type", pic.get("SRC_TYPE"));
				temp.put("title", pic.get("TITLE") == null || pic.get("TITLE").equals("") ? null : pic.get("TITLE") );
				temp.put("url", pic.get("URL"));
				results.add(temp);
			}
		}
		return results;
	}
}
