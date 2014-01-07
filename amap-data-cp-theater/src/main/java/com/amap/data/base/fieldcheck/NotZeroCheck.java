package com.amap.data.base.fieldcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldCheck;
import com.amap.data.base.TempletConfig;

public class NotZeroCheck extends FieldCheck {
	private List<String> fields = new ArrayList<String>();
	private String errorType;

	@SuppressWarnings({ "unchecked" })
	public NotZeroCheck(TempletConfig templet) {
		fields = templet.getList("not_zero_check");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean fieldsCheck(Map to) {
		for (String field : fields) {

			errValue = new HashMap();

			Object value = to.get(field);
			if (ObjectUtil.toInteger(value) == 0
					|| ObjectUtil.toDouble(value) == 0.0
					|| ObjectUtil.toFloat(value) == 0.0
					|| ObjectUtil.toLong(value) == 0) {
				errorType = "字段为0值";
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
		errMessage = "字段非零检查错误";
		return errMessage;
	}
}
