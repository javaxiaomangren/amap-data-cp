package com.amap.data.base.fieldcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.data.base.FieldCheck;
import com.amap.data.base.TempletConfig;

public class NotNullCheck extends FieldCheck {
	private List<String> fields = new ArrayList<String>();
	private String errorType;
	private boolean isWarning;

	@SuppressWarnings({ "unchecked" })
	public NotNullCheck(TempletConfig templet) {
		fields = templet.getList("not_null_check");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean fieldsCheck(Map to) {
		for (String field : fields) {

			errValue = new HashMap();

			Object value = to.get(field);
			if (value == null || value.equals("")) {
				errorType = "字段为空值或空字符串";
				errValue.put(field, value);
				return false;
			}
		}
		return true;
	}

	@Override
	public String getType() {
		return errorType;
	}

	@Override
	public String getErrMessage() {
		errMessage = "字段非空检查错误";
		return errMessage;
	}
	
	@Override
	public boolean getIsWarning() {
		isWarning = false;
		return isWarning;
	}
}
