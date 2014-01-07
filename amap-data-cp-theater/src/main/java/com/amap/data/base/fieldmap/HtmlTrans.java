/**
 * @author caoxuena
 *2012-12-17
 */
package com.amap.data.base.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

import com.amap.data.base.FieldMap;
import com.amap.data.base.TempletConfig;

public class HtmlTrans extends FieldMap {

	private List<String> htmlMap = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public HtmlTrans(TempletConfig templet) {
		// html_map=name,intro
		htmlMap = templet.getList("html_map");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fieldmap(Map from, Map to) {

		//遍历需要去除html标签的各字段
		for(int i = 0; i < htmlMap.size(); i++){
			String ziduanName = htmlMap.get(i);
			Object o = from.get(ziduanName);
			
			String desti = null;
			if(o != null){
				String original = o.toString();
				desti = delHTMLTag(original);
			}
			
			to.put(ziduanName, desti);
		}
		return true;
	}


	@Override
	public String getType() {
		return "html标签替换错误";
	}
	
	/**
	 *  文本取出HTML标签
	 * @author v-helianxin
	 *
	 */
	public static String delHTMLTag(String htmlStr){ 
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
        
        htmlStr = htmlStr.replaceAll("<br>\r", "");
        htmlStr = htmlStr.replaceAll("<br>\n", "\\\n");
        htmlStr = htmlStr.replaceAll("<br>", "\\\n");
        htmlStr = htmlStr.replaceAll("&nbsp;", "");
        htmlStr = htmlStr.replace("?nbsp;", "");

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script=p_script.matcher(htmlStr); 
        htmlStr=m_script.replaceAll(""); //过滤script标签 
        
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style=p_style.matcher(htmlStr); 
        htmlStr=m_style.replaceAll(""); //过滤style标签 
        
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html=p_html.matcher(htmlStr); 
        htmlStr=m_html.replaceAll(""); //过滤html标签 
        
        htmlStr = HtmlUtils.htmlUnescape(htmlStr);
        
        htmlStr = htmlStr.replace("null", "");
        htmlStr = htmlStr.replaceAll("&nbsp;", "");
        htmlStr = htmlStr.replaceAll("(&lt;(?i)(|)[^>]*/?&gt;)|(&nbsp;)|(<[^>]*>)", "");
        
        //替换类似&#8221;
        String regEx = "&#\\d{2,};";
        Pattern p=Pattern.compile(regEx,Pattern.CASE_INSENSITIVE); 
        Matcher m=p.matcher(htmlStr); 
        htmlStr=m.replaceAll(""); //过滤html标签 
        
        if(htmlStr.endsWith("<font color")){
        	htmlStr = htmlStr.replace("<font color", "");
        }

       return htmlStr.trim(); //返回文本字符串 
    } 
	
	//test
	public static void main(String[] args) {
    	String htmlStr="<br>";
    	htmlStr=delHTMLTag(htmlStr);
    	System.out.println(htmlStr);
	}
}
