/**
 * 2013-5-13
 */
package com.amap.data.save.dianping.fieldMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;
import com.amap.data.save.SaveHelper;

/**
 * 动态信息
 */
public class GroupbuyListMap extends FieldMap {
	private List<String> review_rti_cols;
	private List<String> groupbuy_rti_cols;
	private List<String> discount_rti_cols;

	private DecimalFormat df = new DecimalFormat("0.0");

	@SuppressWarnings("unchecked")
	public GroupbuyListMap(TempletConfig templet) {
		type = "动态信息映射";
		review_rti_cols = templet.getList("review_rti_cols");
		groupbuy_rti_cols = templet.getList("groupbuy_rti_cols");
		discount_rti_cols = templet.getList("discount_rti_cols");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean fieldmap(Map from, Map to) {
		List<LinkedHashMap> rtis = new ArrayList<LinkedHashMap>();
		Object market = from.get("market");
		// 当前信息是评论信息
		if ("review".equalsIgnoreCase(market.toString())) {
			if ("0".equals(from.get("review_num"))) {
				return true;
			}
			LinkedHashMap rti = new LinkedHashMap();
			for (String s : review_rti_cols) {
				rti.put(s, from.get(s));
			}
			rtis.add(rti);
		} else if ("groupbuy".equalsIgnoreCase(market.toString())) {
			// 处理团购信息
			if ("0".equals(from.get("groupbuy_num"))) {
				return true;
			}
			List<Map> groupbuys = (List<Map>) from.get("groupbuy_list");
			for (Map groupbuy : groupbuys) {
				groupbuy.put("market", market);
				LinkedHashMap rti = new LinkedHashMap();
				for (String s : groupbuy_rti_cols) {
					if ("group_discount".equalsIgnoreCase(s)) {
						Object group_price_ori = groupbuy
								.get("group_price_ori");
						Object group_price = groupbuy.get("group_price");
						String discount = "0.0";
						if (group_price_ori != null && group_price != null) {
							try {
								discount = df
										.format((Double.parseDouble(group_price
												.toString()) / Double
												.parseDouble(group_price_ori
														.toString())) * 10);
							} catch (Exception e) {
							}
						}
						rti.put(s, discount);
					} else if ("starttime".equalsIgnoreCase(s)) {
						rti.put(s, groupbuy.get("group_time_beg"));
					} else if ("endtime".equalsIgnoreCase(s)) {
						rti.put(s, groupbuy.get("group_time_end"));
					} else {
						rti.put(s, groupbuy.get(s));
					}
				}
				rtis.add(rti);
			}
		} else {
			// 处理优惠信息
			if(from.get("discount_list") == null){
				return true;
			}
			List<Map> disocunts = (List<Map>) from.get("discount_list");
			if(disocunts == null || disocunts.size() == 0){
				return true;
			}
			for (Map disocunt : disocunts) {
				disocunt.put("market", market);
				LinkedHashMap rti = new LinkedHashMap();
				for (String s : discount_rti_cols) {
					if("discount_time_beg".equals(s)){
						rti.put(s, SaveHelper.getTimeFormat(disocunt.get(s).toString(), "yyyy-MM-dd"));
					} else if ("discount_time_end".equals(s)){
						rti.put(s, SaveHelper.getTimeFormat(disocunt.get(s).toString(), "yyyy-MM-dd"));
					} else if ("starttime".equals(s)){
						rti.put(s, SaveHelper.getTimeFormat(disocunt.get(s).toString(), "yyyy-MM-dd"));
					} else if ("endtime".equals(s)){
						rti.put(s, SaveHelper.getTimeFormat(disocunt.get(s).toString(), "yyyy-MM-dd"));
					} else if("pic_info".equals(s)){
						if(disocunt.get(s) != null){
							List<Map> pics = (List<Map>) disocunt.get(s);
							rti.put(s, pics == null || pics.size() == 0 ? null : pics);
						}else{
							rti.put(s, null);
						}
					} else{
						rti.put(s, disocunt.get(s));
					}
				}
				rtis.add(rti);
			}
		}
		to.put("rti", rtis);
		return true;
	}
}
