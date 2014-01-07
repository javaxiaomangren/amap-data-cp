package com.amap.data.base;

import java.util.Map;

public abstract class FieldProc {
	protected String type;
	protected String errMessage;
	@SuppressWarnings("rawtypes")
	protected Map errValue;
	private boolean isWarning = true;
	
	public String getType(){
		return type;
	}
	public String getErrMessage(){
		return errMessage;
	}
	public String getErrValue(){
		if (errValue==null)
			return null;
		else
			return errValue.toString();
	}
	public boolean getIsWarning() {
		return isWarning;
	}
}