package com.amap.data.base;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.fieldcheck.CommonFieldCheck;
import com.amap.data.base.fieldcheck.NotNullCheck;
import com.amap.data.base.fieldcheck.NotZeroCheck;

public class FieldCheckUtil {

	public static List<FieldCheck> genFieldCheckList(TempletConfig templet) {
		List<FieldCheck> lfc = new ArrayList<FieldCheck>();
		
		//一般字段检查
		if (templet.getString("field_check") != null) {
			FieldCheck fc = new CommonFieldCheck(templet);
			lfc.add(fc);
		}
	
		//非空检查
		if (templet.getString("not_null_check") != null) {
			FieldCheck fc = new NotNullCheck(templet);
			lfc.add(fc);
		}
		
		//非零检查
		if (templet.getString("not_zero_check") != null) {
			FieldCheck fc = new NotZeroCheck(templet);
			lfc.add(fc);;
		}
		return lfc;
	}

}
