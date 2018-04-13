package com.hf.core.utils;

import java.util.Enumeration;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;


public class ParamQuickUtil {
	
	/**
	 * 解析request param到map中
	 * @param request
	 * @return
	 */
	public static TreeMap<String, String> getParamMap(HttpServletRequest request){
		TreeMap<String, String> transMap = new TreeMap<String, String>();
		Enumeration<String> enu = request.getParameterNames();
		while(enu.hasMoreElements()) {
			String name = (String)enu.nextElement();
			String value = request.getParameter(name);
			transMap.put(name, value);
		}
		return transMap;
	}

}
