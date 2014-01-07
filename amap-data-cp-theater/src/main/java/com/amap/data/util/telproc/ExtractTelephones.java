package com.amap.data.util.telproc;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractTelephones {

	// 定义常见的电话类型
	private static Set<String> firstType = new LinkedHashSet<String>();

	// 定义非常见电话类型，用于第二次匹配
	private static Set<String> secondType = new LinkedHashSet<String>();

	static {

		String type1 = "\\d{4}\\s?(-|－|—)+\\s?\\d{7,8}";// 4位区号+7位或8位电话号
		String type2 = "\\d{3}\\s?(-|－|—)+\\s?\\d{4}\\s?\\d{4}";// 3位区号+8位电话号
		String type3 = "1\\d{10}";// 11位手机号
		String type4 = "[1-9]\\d{6,7}";// //7位或8位电话号
		firstType.add(type1);
		firstType.add(type2);
		firstType.add(type3);
		firstType.add(type4);

		String type5 = "400\\d{0,1}\\s?-\\s?\\d{2,4}\\s?-\\s?\\d{2,4}";
		String type6 = "00852\\s?-\\s?\\d{7,9}";
		String type7 = "00853\\s?-\\s?\\d{7,9}";
		String type8 = "00886\\s?-\\s?\\d{7,9}";
		String type9 = "800\\s?-\\s?\\d{2,3}\\s?-\\s?\\d{2,4}";
		String type10 = "800\\d{6,7}";
		String type11 = "400\\d{6,7}";
		String type12 = "400\\-\\d{6,7}";
		String type13 = "00886\\s?-\\s?\\d{2,4}\\s?-\\s?\\d{6,8}";
		String type14 = "0\\d{10,11}";
		String type15 = "(086-)({0,1}\\d{3,4}\\s?(-|－|—)+\\s?\\d{7,8})";
		String type16 = "(\\d{3,4}\\s?(-|－|—)+\\s?\\d{7,8})\\s?(-|－|—)+(\\d+)";
		String type17 = "(086-)({1}\\d{3,4}\\s?(-|－|—)+\\s?\\d{7,8})";
		String type18 = "(\\d{3,4}\\s?(-|－|—)+\\s?\\d{7,8}\\s?)(/+\\d{1,})+";
		String type19 = "\\d{3,4}\\s?(-|－|—)\\s?(1\\d{10})";
		String type20 = "400\\s?\\d{4}\\s?\\d{3}";// 400 6811 681
		String type21 = "400\\s?\\d{3}\\s?\\d{4}";// 400 012 0312
		secondType.add(type5);
		secondType.add(type6);
		secondType.add(type7);
		secondType.add(type8);
		secondType.add(type9);
		secondType.add(type10);
		secondType.add(type11);
		secondType.add(type12);
		secondType.add(type13);
		secondType.add(type14);
		secondType.add(type15);
		secondType.add(type16);
		secondType.add(type17);
		secondType.add(type18);
		secondType.add(type19);
		secondType.add(type20);
		secondType.add(type21);
	}

	public static String extractTelephone(String target) {

		// 按照常规格式从target中提取出符合电话号码的字符串
		String result = "";
		String tel = target;
		for (String firstregEx : firstType) {
			Pattern p = Pattern.compile(firstregEx);
			Matcher m = p.matcher(tel);
			while (m.find()) {
				String temp = m.group();
				if (assertPartExact(target, temp)) {
					continue;
				}
				if (result.equals("")) {
					result = temp.trim();
				} else {
					result += ";" + temp.trim();
				}
				tel = tel.replace(temp, "");
				p = Pattern.compile(firstregEx);
				m = p.matcher(tel);
			}
		}

		// 如果result为空，则常规格式没有提取出电话号码，按照第二方式提取
		if (result.equals("")) {
			for (String secondregEx : secondType) {
				Pattern p = Pattern.compile(secondregEx);
				Matcher m = p.matcher(tel);
				while (m.find()) {
					String temp = m.group();
					if (assertPartExact(target, temp)) {
						continue;
					}
					if (result.equals("")) {
						result = temp.trim();
					} else {
						result += ";" + temp.trim();
					}
					tel = tel.replace(temp, "");
					p = Pattern.compile(secondregEx);
					m = p.matcher(tel);
				}
			}
		}

		// 按照target中出现的顺利排列result
		result = ExtractTelephones.resultSort(target, result);

		// 预约QQ：1330904126
		// 针对含QQ的进行特殊处理
		if ((target.contains("QQ") || target.contains("qq"))
				&& !result.equals("")) {
			result = ExtractTelephones.qqFilter(target, result);
		}

		// 去掉result后面的分号：020-23349902;;
		while (result.endsWith(";")) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	// 判断是否只抽取了一部分数字，比如1.029-29-87857593，是否只抽取了满足条件的87857593；2.预约QQ：1330904126是否只抽误了13309041
	// 是的话则返回true
	// 补充：针对“010-65924969（至少提前1-2天预约）,010-84477223010-65924969”或“010-84477223010-65924969，010-65924969（至少提前1-2天预约）”，通过判断其在target中的index进行比较是否是一部分
	public static boolean assertPartExact(String target, String tel) {
		// 针对特殊情况进行处理
		if(target.startsWith(tel)){
			//针对“010-65924969（至少提前1-2天预约）,010-84477223010-65924969”
			String regEx = "(" + tel + "\\D{1,})";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(target);
			if (m.find()) {
				return false;
			}
		}else{
			//针对“010-84477223010-65924969，010-65924969（至少提前1-2天预约）”
			String regEx = "((，|,)" + tel + "\\D{2,})";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(target);
			if (m.find()) {
				return false;
			}
		}
		

		// 首先判断是否只抽取了后半部分
		String regEx = "(\\d{1,}(-|－|—){0,}" + tel + ")";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(target);
		if (m.find()) {
			return true;
		}

		// 其次判断是否只抽取了前半部分
		regEx = "(" + tel + "(-|－|—){0,}\\d{1,})";
		p = Pattern.compile(regEx);
		m = p.matcher(target);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 滤除提取出的QQ号码:预约QQ：1330904126,预约QQ：25240604、15093583352
	// 原则：把紧挨着QQ的号码从result中删除
	public static String qqFilter(String target, String result) {
		String[] fields = result.split(";");

		// 定义flag，用于标识当前结果中存储的号码是否是QQ号，初始都是false，表明不是QQ
		boolean[] flags = new boolean[fields.length];
		for (int i = 0; i < flags.length; i++) {
			flags[i] = false;
		}

		// 从大范围开始构建正则表达式，找到在QQ后面紧跟的所有号码
		for (int i = 0; i < fields.length; i++) {
			for (int j = fields.length - 1; j >= i; j--) {
				String combine = "";
				if (i == j) {
					combine = fields[i];
					flags[i] = true;
				} else {
					for (int k = i; k <= j; k++) {
						if (k == i) {
							combine += fields[k];
						} else {
							combine += "(、|,| )" + fields[k];
						}
						flags[k] = true;
					}
				}

				// 构建正则表达式
				String regEx = "((QQ|qq)(：|:|)" + combine + ")";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(target);
				// 判断结果是QQ号，则从结果中删除
				if (m.find()) {
					// 从结果result中删除标志位true的项
					for (int n = 0; n < flags.length; n++) {
						if (flags[n]) {
							result = result.replace(fields[n], "");
						}
					}
				} else {// 不是QQ号
					for (int n = 0; n < flags.length; n++) {
						flags[n] = false;
					}
				}
			}
		}
		return result;
	}

	// 按照target中出现的顺序对result中的结果进行排序
	public static String resultSort(String target, String result) {
		String[] fields = result.split(";");

		// 冒泡排序
		for (int i = 0; i < fields.length - 1; i++) {
			for (int j = i + 1; j < fields.length; j++) {
				if (target.indexOf(fields[i]) > target.indexOf(fields[j])) {
					String temp = fields[i];
					fields[i] = fields[j];
					fields[j] = temp;
				}
			}
		}

		// 排序后重组result
		String newResult = "";
		for (int i = 0; i < fields.length; i++) {
			if (i == 0) {
				newResult += fields[i];
			} else {
				newResult += ";" + fields[i];
			}
		}
		return newResult;
	}

	public static void main(String[] args) {
		// 测试代码
//		 String bb =
//		 "0280-20-8482269230  ,020-83387209/13560230328,020-83373207/83387209/13560230328";
//		String bb = "010-84477223010-65924969（至少提前1-2天预约）,010-64986783010-65924969（至少提前1-2天预约）,010-65924969（至少提前1-2天预约）";
//		String bb = "010-65924969（至少提前1-2天预约）,010-84477223010-65924969（至少提前1-2天预约）,010-64986783010-65924969（至少提前1-2天预约）";

//		 String bb = "020-34876789,预约qq25240604、15093583352";
//		 String bb = "400-0000-260（提前1天预约）";
//		 String bb = "400 6811 681（提前1天预约）";
//		 String bb = "010-6200 3502";
		// String bb = "0531-880898888（提";
		// String bb = "029-29-87857593（提前1天预约）";
//		 String bb = "021--36309589,13321807096　　　,021-32221633";
		// String bb =
		// "020-23349902（请提前2-3天预约）,预约QQ：13309041261、13309041262（请提前3天加Q预约）";
//		 String bb = "0571-56679526/28005099,0571-88291737‎,13606649813";
//		 String bb = "0512-55251777  55251779  55251779（提前1天预约）";
		// String bb = "13210201817  13608986596（提前1-2天预约）";
		 String bb = "400-013-8128,020-34876789,15093583352预约qq25240604、15093583353";

		String result = ExtractTelephones.extractTelephone(bb);
		System.out.println(result);
	}
}
