/**
 * 2013-7-18
 */
package com.amap.theater.staticnum;

import com.amap.base.data.DBDataReader;

import java.util.List;
import java.util.Map;

/**
 * 统计大麦网深度信息完备率
 */
public class StaticDeep {
	private static int totalNum = 0;
	private static int matchNum = 0;
	
	private static int matchIntro = 0;
	
	private static int matchPic = 0;
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args){
		List<Map> datas = getDatas();
		totalNum = datas.size();
		
		for(Map data : datas){
			//判断是否匹配上
			if(assertMatch(data)){
				matchNum++;
				if(assertIntro(data)){
					matchIntro++;
				}
				if(assertPic(data)){
					matchPic++;
				}
			}
		}
		
		System.out.println("大麦网总深度个数为：" + totalNum + ", 匹配上的个数为：" + matchNum);
		System.out.println("匹配上的有简介的个数为：" + matchIntro);
		System.out.println("匹配上的有图片的个数为：" + matchPic);
		
	}
	
	//判断图片是否有值
	@SuppressWarnings("rawtypes")
	private static boolean assertPic(Map data){
		Object pic = data.get("pic");
		if(pic == null || pic.equals("")){
			pic = data.get("pic2");
			if(pic == null || pic.equals("")){
				return false;
			}
		}
		return true;
	}
	
	//判断intro是否有值
	@SuppressWarnings("rawtypes")
	private static boolean assertIntro(Map data){
		Object intro = data.get("text");
		if(intro == null || intro.equals("") || intro.equals("暂无")){
			return false;
		}
		return true;
	}
	
	//判断是否匹配上
	@SuppressWarnings("rawtypes")
	private static boolean assertMatch(Map data){
		Object poiid = data.get("poiid");
		if(poiid == null || poiid.equals("") || poiid.equals("null")){
			return false;
		}
		return true;
	}
	
	//获取全部数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Map> getDatas(){
		String sql = "select * from theatre_damai";
		DBDataReader ddr = new DBDataReader(sql);
		ddr.setDbenv(null);
		return ddr.readAll();
	}
}
