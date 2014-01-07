package com.amap.data.base.fieldcheck;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.base.utils.ObjectUtil;
import com.amap.data.base.FieldCheck;
import com.amap.data.base.TempletConfig;

public class CommonFieldCheck extends FieldCheck {
	@SuppressWarnings("rawtypes")
	private List<Map> fields = new ArrayList<Map>();
	private String errorType;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CommonFieldCheck(TempletConfig templet) {
		List<String> fm = templet.getList("field_check");
		for (String s1 : fm) {
			Map m = new HashMap();
			String[] t2 = s1.split("\\|");
			for (String s2 : t2) {
				String[] t3 = s2.split("-");
				m.put(t3[0], t3[1]);
			}
			fields.add(m);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public boolean fieldsCheck(Map to) {
		for (Map field : fields) {
			
			errValue = new HashMap();

			String col = ObjectUtil.toString(field.get("name"));
			
			Object value = to.get(col);
			if (value == null || value.equals("")) {
				to.put(field.get("name"), null);
				continue;
			}

			String type = ObjectUtil.toString(field.get("type"));
			int len = Integer.parseInt(ObjectUtil.toString(field.get("len")));

			// cxn add and modify:judge the value's lenth
			FieldLenCheck fieldLenCheck = new FieldLenCheck();

			if ("varchar".equals(type)) {

				if (value.toString().length() > len) {
					errorType = "varchar长度太长";
					errValue.put(col,value);
					return false;
				}
			} else if ("text".equals(type) || "mediumtext".equals(type)) {
				//text类型不做长度检查
			} else if ("int".equals(type)) {
				try {
					int i = Integer.parseInt(ObjectUtil.toString(value));
					if (!fieldLenCheck.IntJudge(i)) {
						errorType = "int is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "int type error";
					errValue.put(col,value);
					return false;
				}
			} else if ("smallint".equals(type)) {
				try {
					int i = Integer.parseInt(ObjectUtil.toString(value));
					if (!fieldLenCheck.SmallintJudge(i)) {
						errorType = "smallint is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "smallint type error";
					errValue.put(col,value);
					return false;
				}
			} else if ("bigint".equals(type)) {
				try {
					long l = Long.parseLong(ObjectUtil.toString(value));
					if (!fieldLenCheck.BigintJudge(l)) {
						errorType = "bigint is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "bigint type error";
					errValue.put(col,value);
					return false;
				}

			} else if ("timestamp".equals(type)) {
				try {
					long l = Long.parseLong(ObjectUtil.toString(value));
					if (!fieldLenCheck.BigintJudge(l)) {
						errorType = "datatime is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "bigint type error";
					errValue.put(col,value);
					return false;
				}

			} else if ("double".equals(type)) {
				try {
					double d = Double.parseDouble(ObjectUtil.toString(value));
					if (!fieldLenCheck.DoubleJudge(d)) {
						errorType = "double is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "double type error";
					errValue.put(col,value);
					return false;
				}
			} else if ("float".equals(type)) {
				try {
					float f = Float.parseFloat(ObjectUtil.toString(value));
					if (!fieldLenCheck.FloatJudge(f)) {
						errorType = "float is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "float type error";
					errValue.put(col,value);
					return false;
				}
			} else if ("datetime".equals(type)) {
				try {
					Date date = null;
					DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
					DateFormat format2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					date = format1.parse(ObjectUtil.toString(value)); 
					date = format2.parse(ObjectUtil.toString(value)); 
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "datetime type error";
					errValue.put(col,value);
					return false;
				}
			} else if ("char".equals(type)) {
				if (fieldLenCheck.GetVarcharLenth(value.toString()) > len) {
					errorType = "char is out of range";
					errValue.put(col,value);
					return false;
				}

			} else if ("tinyint".equals(type)) {
				try {
					int i = Integer.parseInt(ObjectUtil.toString(value));
					if (!fieldLenCheck.TinyintJudge(i)) {
						errorType = "tinyint is out of range";
						errValue.put(col,value);
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorType = "tinyint type error";
					errValue.put(col,value);
					return false;
				}
			} else {
				errorType = "type error";
				errValue.put(col,value);
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
	public String getErrMessage(){
		errMessage = "字段类型检查错误";
		return errMessage;
	}
}
