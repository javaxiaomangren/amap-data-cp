package com.si.utils;

import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;

public class XmlAndJsonUtil {
	@SuppressWarnings("unchecked")
	public static String xml2Json(String xml) throws DocumentException {
		Document document = DocumentHelper.parseText(xml); ;
		Element rootElt = document.getRootElement(); // 获取根节�?
		Iterator iter = rootElt.elementIterator("DATA");
		JSONObject json = new JSONObject();
		while(iter.hasNext()) {
			  Element element=(Element)iter.next();
			  String action = element.attributeValue("NAME");
			  String value = element.getText();
			  json.put(action, value);
		}
		return json.toString();
	}
}
