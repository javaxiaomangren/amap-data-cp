package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.JsonUtil;
import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class ImgMap extends FieldMap {

	private String srcType;
	private List<String> imgMap = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public ImgMap(TempletConfig templet) {
		srcType = templet.getString("src_type");
		imgMap = templet.getList("picinfo_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {
		Map pic = new HashMap();

		if (imgMap != null) {
			// 首先判断content中是否包含pictures信息，如果包含并且非空的话从content中取url信息
			List<Map> pic_infos = null;
			if(imgMap.get(0) != null && !imgMap.get(0).equals("")){
				pic_infos = new ArrayList<Map>();
				pic_infos = assertContent(from, imgMap.get(0), srcType);
			}
			if (pic_infos != null) {
				to.put("pic_info", pic_infos);
			} else {
				// content中不包含picture信息，才从img中获取
				Object url = from.get(imgMap.get(1));

				if (url != null && !url.equals("")) {
					// img中图片信息不为空
					pic.put("url", url);
					pic.put("title", null);
					pic.put("src_type", srcType);
					pic.put("iscover", 0);
					pic_infos = new ArrayList<Map>();
					pic_infos.add(pic);
					to.put("pic_info", pic_infos);
				} else {
					// img中图片信息为空
					to.put("pic_info", null);
				}
			}
		} else {
			to.put("pic_info", null);
		}

		return true;
	}

	// 判断from中的content是否包含pictures信息
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> assertContent(Map from, String getziduan, String src_type) {
		List<Map> pic_info = new ArrayList<Map>();
		if (from.containsKey(getziduan)) {
			// 取出content信息并强制转成map
			String contentObj = from.get(getziduan).toString();
			Map content = JsonUtil.parseMap(contentObj);

			if (content != null && content.containsKey("Pictures")) {
				Object picturesObj = content.get("Pictures");

				// 判断是否是list
				List l = new ArrayList();
				l = (List) picturesObj;
				if (l != null && l.size() != 0) {
					for (int i = 0; i < l.size(); i++) {
						Map temp = (Map) l.get(i);
						Map pic_infotemp = new HashMap();
						pic_infotemp.put("url", temp.get("Url"));
						pic_infotemp.put("title", temp.get("Title"));
						pic_infotemp.put("iscover", 0);
						pic_infotemp.put("src_type", src_type);

						pic_info.add(pic_infotemp);
					}

					return pic_info;
				} else {
					// 判断是否是Map
					Map m = new HashMap();
					m = (Map) picturesObj;
					if (m != null && m.size() != 0) {
						Map pic_infotemp = new HashMap();
						pic_infotemp.put("url", m.get("Url"));
						pic_infotemp.put("title", m.get("Title"));
						pic_infotemp.put("iscover", 0);
						pic_infotemp.put("src_type", src_type);

						pic_info.add(pic_infotemp);

						return pic_info;
					}
				}
			}// end if
		}
		return null;
	}
}
