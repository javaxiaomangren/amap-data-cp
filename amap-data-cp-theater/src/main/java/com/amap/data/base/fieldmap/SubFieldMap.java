package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.JsonUtil;
import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;

public class SubFieldMap extends FieldMap {
	private List<String> fm;
	private List<String> ziduanName = new ArrayList<String>();
	private List<String> deepinfos = new ArrayList<String>();
	private int dealNum;

	@SuppressWarnings("unchecked")
	public SubFieldMap(TempletConfig templet) {
		type = "subfield映射";

		// deepinfo_map=address-deepinfo.des.price,
		fm = templet.getList("subfield_map");
		dealNum = fm.size();

		for (int i = 0; i < dealNum; i++) {
			ziduanName.add(fm.get(i).split("-")[0]);
			deepinfos.add(fm.get(i).split("-")[1]);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		for (int i = 0; i < dealNum; i++) {
			String[] fields = deepinfos.get(i).split("\\.");
			if (fields.length < 2) {// 长度最短为2，即类似deepinfo.des
				errMessage = "长度最短为2,少写字段";
				errValue = from;
				return false;
			}
			
			Object toInfo = null;
			// 来源字段信息
			String deepinfo = ObjectUtil.toString(from.get(fields[0]));
			if(deepinfo != null && !deepinfo.equals("")){
				deepinfo = deepinfo.replaceAll("\\s*\":", "\":");
				toInfo = getToInfo(deepinfo, fields);
			}

			to.put(ziduanName.get(i), toInfo);
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access", "finally" })
	public static Object getToInfo(String deepinfo, String[] fields) {
		Object toInfo = null;
		JsonUtil jsonUtil = new JsonUtil();

		int length = 1;

		try {
			Map info = jsonUtil.parseMap(SaveHelper.getTranserJson(deepinfo));

			Object temp = null;
			do {
				// 字段名统一为小写
				Map map = new HashMap();
				for (Object o : info.keySet()) {
					String s = (String) o;
					String sl = s.toLowerCase();
					map.put(sl, info.get(o));
				}
				info = map;
				if (info.get(fields[length]) != null) {
					temp = info.get(fields[length]);
					try{
						Map toInfoMap = (Map) temp;
						toInfoMap = SaveHelper.transferToSmall(toInfoMap);
						toInfo = toInfoMap;
					}catch (Exception e) {
						try{
							List<Map> toInfoMap = (List<Map>) temp;
							toInfoMap = SaveHelper.transferToSmall(toInfoMap);
							toInfo = toInfoMap;
						}catch (Exception e1) {
							toInfo = temp;
						}
					}
				}else{
					toInfo = null;
				}

				length++;

				if (temp != null && length < fields.length) {

					// 判断是否是list
					List l = new ArrayList();
					try {
						l = (List) temp;
						if (l != null && l.size() != 0) {
							// 如果是list，只取第一个
							Object l1 = l.get(0);
							info = (Map) l1;
						} 
					} catch (Exception e) {
						// nothing
					}
					
					try {
						// 判断是否是Map
						Map m = new HashMap();
						m = (Map) temp;
						if (m != null && m.size() != 0) {
							info = m;
						}
					} catch (Exception e) {
					}
				}
			} while (length < fields.length);
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			return toInfo == null || toInfo.equals("") ? null : toInfo;
		}
	}

	public static void main(String[] args) {
		String deepinfo = "{\"VenueContent\": \"深圳的面演出。\", \"Pictures\": [{\"Url\": \"http://static.damai.cn/Damai/VenuePic/ec323183-95a9-4a6e-995b-c4d11de3137a.jpg\", \"Type\": \"\", \"Title\": \"\"}], \"sourceinfo\": {\"logo\": \"/attachment/coop_logos/damai.png\", \"cn\": \"大麦网\", \"eng\": \"damai\"}}";
		String[] fields = new String[2];
		fields[0] = "deepinfo";
		fields[1] = "pictures";
//		fields[2] = "url";

		Object result = getToInfo(deepinfo, fields);
		System.out.println(result);
	}
}
