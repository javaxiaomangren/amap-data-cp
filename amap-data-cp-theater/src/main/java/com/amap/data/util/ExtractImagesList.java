package com.amap.data.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtractImagesList {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> extractImagesList(String str, String head,
			String tail) {
		List<String> l = new ArrayList();
		while (str.indexOf(head) >= 0 && str.indexOf(tail) >= 0) {

			int left = str.indexOf(head);
			int next = str.indexOf(head, left + 1);
			int right = str.indexOf(tail) + tail.length();
			String element = null;

			// 如果是最后一个http头，将str置为空
			if (left == str.lastIndexOf(head)) {
				element = str.substring(left, right);
				l.add(element);
				break;
			}
			// 否则判断后缀是否落在两个http头中间，如果落入，则将str挪到第二个http头处
			else if (left < right && right < next) {
				element = str.substring(left, right);
			}

			if (element != null) {
				l.add(element);
			}

			// 将指针挪到下一个http头处
			str = str.substring(next);

		}
		return l;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> extractImagesList(String str, String head,
			List<String> tailList) {
		List<String> l = new ArrayList();
		for (String tail : tailList) {
			List l1 = extractImagesList(str, head, tail);
			l.addAll(l1);
		}
		return l;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static void main(String[] args) {
		String s = "[ {max:'http://images3png.com/hotelimage/114039/BAA1F4F2-6170-4867-BFCD-7DA5A75F15E4/550/370.gif',min:'http://images3.ctrip.com/hotelimage/114039/BAA1F4F2-6170-4867-BFCD-7DA5A75F15E4/78/58.jpg',title:'南京肯定宾馆（奥体中心店）外观'},{max:'http://images3.ctrip.com/hotelimage/114039/A9980815-6CB4-4BC1-A69D-17BB6436FBA9/550/370.jpg',min:'http://images3.ctrip.com/hotelimage/114039/A9980815-6CB4-4BC1-A69D-17BB6436FBA9/78/58.jpg',title:'南京肯定宾馆（奥体中心店）前台'},{max:'http://images3.ctrip.com/hotelimage/114039/893F1C96-1E69-4F43-82B8-06B8F6F96919/550/370.jpg',min:'http://images3.ctrip.com/hotelimage/114039/893F1C96-1E69-4F43-82B8-06B8F6F96919/78/58.jpg',title:'南京肯定宾馆（奥体中心店）外观'},{max:'http://images3.ctrip.com/hotelimage/114039/BF8517E8-2C94-4289-8780-E3AB2F0001B6/550/370.jpg',min:'http://images3.ctrip.com/hotelimage/114039/BF8517E8-2C94-4289-8780-E3AB2F0001B6/78/58.jpg',title:'南京肯定宾馆（奥体中心店）走廊'},{max:'http://images3.ctrip.com/hotelimage/114039/7E6CCBC2-A715-45E0-94AC-56F318BFC298/550/370.jpg',min:'http://images3.ctrip.com/hotelimage/114039/7E6CCBC2-A715-45E0-94AC-56F318BFC298/78/58.jpg',title:'南京肯定宾馆（奥体中心店）走廊'} ];";
		String tt="6080：http://static.damai.cn/http_imgload.aspx?p=afe2a6e2cf1ede1fa4bc1f7ccfd01e4b1f5bedcdb30d1fb07daf1fd2b07d8a4ba9b05ece0dafb00d5e7dededcd8ab3e25ea90d；6077：http://static.damai.cn/http_imgload.aspx?p=afe2a6e2cf1ede1fa4bc1f7ccfd01e7d5ed0b37dcece7db0b34bcd7db07dd25bafb0b31fedd0b00d5b5e4b5bb37dededed8aed；6079：http://static.damai.cn/http_imgload.aspx?p=afe2a6e2cf1ede1fa4bc1f7ccfd01eb37d1fedd2ed1f5bb00dafd2a9b07dcdd2afb04baf8acdb05beda95ea98a7d8aced2afce；6078：http://static.damai.cn/http_imgload.aspx?p=afe2a6e2cf1ede1fa4bc1f7ccfd01ea9e25ed2b3cd5e7db0e2d07dd2b07d5b8ad0b05ece7db3b07d4b5b7d0d4bd04b1f8acd5e；";
		String head = "http://";
		String tail = "；";
		List<String> tailList = new ArrayList();
		tailList = Arrays.asList(new String[] { ".jpg", ".gif", ".swf",".png" ,"；"});
		System.out.print(ExtractImagesList.extractImagesList(tt, head,"；"));
//		for (String s1 : extractImagesList(tt, head, tailList)) {
//			System.out.println(s1);
//		}
		
	}
}
