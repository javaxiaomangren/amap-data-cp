package com.si.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class FileDateComparable  implements Comparator<File>{
	public int compare(File f1,File f2) {
		String f1Name = f1.getName();
		String f2Name = f2.getName();
		Date f1date = isDate(f1Name);
		Date f2date = isDate(f2Name);
		int tmp = 0;
		if(f1date!=null && f2date!=null) {
			if(f1date.after(f2date)) {
				tmp = 1;
			}else if(f1date.before(f2date)) {
				tmp = -1;
			}else {
				tmp = 0;
			}
		}
		return tmp;
	}
	

	private Date isDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.setLenient(false);
			
			return sdf.parse(str);
		} catch (ParseException e) {
			//log.error(e.getMessage());
			return null;
		}
	}
}
