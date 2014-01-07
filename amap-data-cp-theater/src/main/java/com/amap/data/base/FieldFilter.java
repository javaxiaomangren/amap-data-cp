package com.amap.data.base;

import java.util.Map;


public abstract class FieldFilter extends FieldProc {
	@SuppressWarnings("rawtypes")
	public abstract boolean fieldfilter(Map from);
}

