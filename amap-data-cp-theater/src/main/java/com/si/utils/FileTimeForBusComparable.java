package com.si.utils;

import com.amap.base.utils.ObjectUtil;

import java.io.File;
import java.util.Comparator;


public class FileTimeForBusComparable  implements Comparator<File>{
	
	public int compare(File f1,File f2) {
		String f1Name = f1.getName();
		String f2Name = f2.getName();
		int tmp = 0;
		if(f1Name.indexOf("_")>0 && f2Name.indexOf("_")>0) {
			String[] f1array = f1Name.split("_");
			String[] f2array = f2Name.split("_");
			if(f1array!=null && f1array.length>0 && f2array!=null && f2array.length>0 ) {
				int f1num = ObjectUtil.toInteger(f1array[0]);
				int f2num = ObjectUtil.toInteger(f2array[0]);
				int f1batchnum = ObjectUtil.toInteger(f1array[1]);
				int f2batchnum = ObjectUtil.toInteger(f2array[1]);
				if(f1num>f2num) {
					tmp = 1;
				}else if(f1num==f2num) {
					if(f1batchnum>f2batchnum) {
						tmp = 1;
					}else if(f1batchnum==f2batchnum) {
						if(f1Name.indexOf("add")>0 && f2Name.indexOf("modify")>0) {
							tmp = -1;
						}else if(f1Name.indexOf("add")>0 && f2Name.indexOf("delete")>0)  {
							tmp = -1;
						}else if(f1Name.indexOf("add")>0 && f2Name.indexOf("add")>0)  {
							tmp = 0;
						}else if(f1Name.indexOf("modify")>0 && f2Name.indexOf("add")>0)  {
							tmp = 1;
						}else if(f1Name.indexOf("modify")>0 && f2Name.indexOf("delete")>0)  {
							tmp = -1;
						}else if(f1Name.indexOf("modify")>0 && f2Name.indexOf("modify")>0)  {
							tmp = 0;
						}else if(f1Name.indexOf("delete")>0 && f2Name.indexOf("add")>0)  {
							tmp = 1;
						}else if(f1Name.indexOf("delete")>0 && f2Name.indexOf("modify")>0)  {
							tmp = 1;
						}else if(f1Name.indexOf("delete")>0 && f2Name.indexOf("delete")>0)  {
							tmp = 0;
						} 
					}else {
						tmp = -1;
					}
					
				}else {
					tmp = -1;
				}
			}
		}
		return tmp;
	}
}
