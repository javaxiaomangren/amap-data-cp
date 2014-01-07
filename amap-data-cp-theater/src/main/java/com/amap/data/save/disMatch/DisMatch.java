/**
 * 2013-7-12
 */
package com.amap.data.save.disMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amap.base.data.DBDataReader;
import com.amap.base.data.DBExec;
import com.amap.data.save.SaveHelper;
import com.amap.data.save.add.AddSave;
import com.amap.data.save.baidu.QingbaoAddSave;
import com.amap.data.save.car.CarSave;
import com.amap.data.save.cinema.CinemaSave;
import com.amap.data.save.cinemamerge.CinemaMergeSave;
import com.amap.data.save.ctripwireless.CtripWirelessSave;
import com.amap.data.save.diandian.DiandianSave;
import com.amap.data.save.dianping.DianpingSave;
import com.amap.data.save.dianpingrefresh.DianpingrefreshSave;
import com.amap.data.save.dingding.DingdingSave;
import com.amap.data.save.elong.ElongSave;
import com.amap.data.save.event.EventSave;
import com.amap.data.save.golf.GolfSave;
import com.amap.data.save.haobai.HaobaiSave;
import com.amap.data.save.hotelvp.HotelVpSave;
import com.amap.data.save.jingpin.JingpinSave;
import com.amap.data.save.juheoil.JuheoilSave;
import com.amap.data.save.lashou.LashouSave;
import com.amap.data.save.like.LikeSave;
import com.amap.data.save.lnunicom.LnunicomSave;
import com.amap.data.save.meituan.MeituanSave;
import com.amap.data.save.menzhi.MenzhiSave;
import com.amap.data.save.roadcross.RoadcrossSave;
import com.amap.data.save.scenic.ScenicSave;
import com.amap.data.save.sndt.SndtSave;
import com.amap.data.save.theater.TheaterSave;
import com.amap.data.save.tongcheng.TongchengSave;
import com.amap.data.save.tuan800.Tuan800Save;
import com.amap.data.save.tujia.TujiaSave;
import com.amap.data.save.tuniu.TuniuSave;
import com.amap.data.save.xiaomishu.XiaomishuSave;

public class DisMatch {
	private final static Logger log = LoggerFactory.getLogger(DisMatch.class);
	private final static String error_table = "poi_error";
	// 用于标记数据是入正式环境还是测试环境，true代表入测试环境，false代表入正式环境
	private static boolean test_flag = true;
	protected static String update_flag = "update_flag";
	
	//用于记录正确处理之后需要初始化标志位的sql
	private static List<String> sqlList = new ArrayList<String>();
	
	@SuppressWarnings("rawtypes")
	public static void deal(){
		initUrlFlag();
		List<Map> errInfos = getErrorInfo();
		dealDisMatch(errInfos);
		setUpdateFlag();
	}

	/**
	 * 初始化参数
	 */
	private static void initUrlFlag() {
		if (test_flag) {
			update_flag = "test_update_flag";
		}
	}

	/**
	 * 从错误表中获取本次需要纠正的所有数据信息：cp、id和poiid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getErrorInfo() {
		String sql = "select * from " + error_table + " where " + update_flag
				+ " != 0 and poiid is not null and poiid != '' and poiid != 'null'";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		List<Map> errInfos = ddr.readAll();
		log.info("本次错误匹配的数据共有：" + errInfos.size());
		return errInfos;
	}

	/**
	 * 对获取到的错误匹配数据进行处理：原来错误的下线，并且更新错误表中的标记位
	 */
	@SuppressWarnings("rawtypes")
	private static boolean dealDisMatch(List<Map> errInfos) {
		for(Map errInfo : errInfos){
			dealDisMatchEach(errInfo);
		}
		return true;
	}
	
	/**
	 * 单条处理错误匹配数据
	 */
	@SuppressWarnings("rawtypes")
	private static boolean dealDisMatchEach(Map errInfo){
		String cp = null, poiid = null, id = null;
		try{
			cp = errInfo.get("cp").toString();
			poiid = errInfo.get("poiid").toString();
			id = errInfo.get("id").toString();
		}catch (Exception e) {
			log.info("错误表中cp、poiid和id存在为空的情况，当前传入的信息是：" + errInfo);
			return false;
		}
		if(cp.equalsIgnoreCase("null") || poiid.equalsIgnoreCase("null") || id.equalsIgnoreCase("null")){
			log.info("错误表中cp、poiid和id存在为空的情况，当前传入的信息是：" + errInfo);
			return false;
		}
		
		boolean flag = true;
		if ("car_bitauto_api".equalsIgnoreCase(cp)){
			flag = new CarSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("cinema_kokozu_api".equalsIgnoreCase(cp)){
			flag = new CinemaSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("hotel_ctrip_wireless_api".equalsIgnoreCase(cp)){
			flag = new CtripWirelessSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("dining_dianping_api".equalsIgnoreCase(cp)){
			flag = new DianpingSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("cinema_dianping_api".equalsIgnoreCase(cp)){
			flag = new DianpingSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("discount_dingding_api".equalsIgnoreCase(cp)){
			flag = new DingdingSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("hotel_elong_api".equalsIgnoreCase(cp)){
			flag = new ElongSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("groupbuy_lashou_api".equalsIgnoreCase(cp)){
			flag = new LashouSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("theater_damai_api".equalsIgnoreCase(cp)){
			flag = new TheaterSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("groupbuy_tuan800_api".equalsIgnoreCase(cp)){
			flag = new Tuan800Save().dealSave(cp, false, poiid, id, test_flag);
		} else if ("dining_xiaomishu_api".equalsIgnoreCase(cp)){
			flag = new XiaomishuSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("groupbuy_meituan_api".equalsIgnoreCase(cp)){
			flag = new MeituanSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("scenic_yikuaiqu_api".equalsIgnoreCase(cp)){
			flag = new ScenicSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("ali_qqfood".equalsIgnoreCase(cp) || "ali_qunar".equalsIgnoreCase(cp) || "ali_fantong".equalsIgnoreCase(cp) 
				 || "ali_kaifanla".equalsIgnoreCase(cp) || "ali_sinahouse".equalsIgnoreCase(cp)
				 || "ali_soufun".equalsIgnoreCase(cp)){
			flag = new AddSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("qingbao_base".equalsIgnoreCase(cp)){
			flag = new QingbaoAddSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("golf_yungao_api".equalsIgnoreCase(cp)){
			flag = new GolfSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("hospital_dianping_api".equalsIgnoreCase(cp) || "hotel_dianping_api".equalsIgnoreCase(cp) || "education_dianping_api".equalsIgnoreCase(cp)
				 || "shopping_dianping_api".equalsIgnoreCase(cp) || "enjoy_dianping_api".equalsIgnoreCase(cp) || "scenic_dianping_api".equalsIgnoreCase(cp)
				 || "dianping_api".equalsIgnoreCase(cp) || "building_dianping_api".equalsIgnoreCase(cp) || "residential_dianping_api".equalsIgnoreCase(cp)
				 || "car_dianping_api".equalsIgnoreCase(cp) || "cinema_dianping_api".equalsIgnoreCase(cp) || "theater_dianping_api".equalsIgnoreCase(cp)){
			flag = new DianpingSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("sndt".equalsIgnoreCase(cp)){
			flag = new SndtSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("hotel_hotelvp_api".equalsIgnoreCase(cp)){
			flag = new HotelVpSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("road_cross".equalsIgnoreCase(cp)){
			flag = new RoadcrossSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("dining_diandian_api".equalsIgnoreCase(cp)){
			flag = new DiandianSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("groupbuy_like_api".equalsIgnoreCase(cp)){
			flag = new LikeSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("site_collect".equalsIgnoreCase(cp)){
			flag = new MenzhiSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("scenic_tuniu_api".equalsIgnoreCase(cp)){
			flag = new TuniuSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("scenic_17u_api".equalsIgnoreCase(cp)){
			flag = new TongchengSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("jingpin_special_shop".equalsIgnoreCase(cp)){
			flag = new JingpinSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("event_poi".equalsIgnoreCase(cp)){
			flag = new EventSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("cms_cinema_merge".equalsIgnoreCase(cp)){
			flag = new CinemaMergeSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("business_hall_lnunicom".equalsIgnoreCase(cp)){
			flag = new LnunicomSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("dianxin_haobai".equalsIgnoreCase(cp)){
			flag = new HaobaiSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("hotel_tujia_api".equalsIgnoreCase(cp)){
			flag = new TujiaSave().dealSave(cp, true, poiid, id, test_flag);
		} else if ("juhe_oil_api".equalsIgnoreCase(cp)){
			flag = new JuheoilSave().dealSave(cp, false, poiid, id, test_flag);
		} else if ("dianping_refresh".equalsIgnoreCase(cp)){
			flag = new DianpingrefreshSave().dealSave(cp, false, poiid, id, test_flag);
		} else {
			flag = false;
			log.info("当前cp不在处理范围内，当前cp是：" + cp);
		}
		
		if(flag){
			String sql = "update " + error_table + " set " + update_flag + " = 0, updatetime = '"
					+ SaveHelper.getUpdateTime() + "' where cp = '" + cp
					+ "' and poiid = '" +poiid + "' and id = '" + id + "'";;
			sqlList.add(sql);
		}
		return flag;
	}
	
	/**
	 * 处理之后，对应的标记位清空
	 */
	private static void setUpdateFlag(){
		DBExec dbexec = new DBExec();
		dbexec.setDbenv(null);
		dbexec.setSqlList(sqlList);
		dbexec.dbExec();
	}
}
