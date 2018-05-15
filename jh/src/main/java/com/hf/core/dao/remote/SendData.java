package com.hf.core.dao.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import org.apache.commons.lang3.StringUtils;


public class SendData {
 
	  
    /** 
     * 将map转为json 
     * @param map 
     * @param sb 
     * @return 
     */  
    public static StringBuilder mapToJson(Map<?, ?> map, StringBuilder sb) {  
        if (sb == null) sb = new StringBuilder();  
        sb.append("{");  
        Iterator<?> iter = map.entrySet().iterator();  
        while (iter.hasNext()) {   
            Entry<?, ?> entry = (Entry<?, ?>) iter.next();  
            String key = entry.getKey() != null ? entry.getKey().toString() : "";  
            sb.append("\"" + stringToJson(key) + "\":");  
            Object o = entry.getValue();  
            if (o instanceof List<?>) {  
                List<?> l = (List<?>) o;  
                listToJson(l, sb);  
            } else if (o instanceof Map<?, ?>) {  
                Map<?, ?> m = (Map<?, ?>) o;  
                mapToJson(m, sb);  
            } else {  
                String val = entry.getValue() != null ? entry.getValue().toString() : "";  
                sb.append("\"" + stringToJson(val) + "\"");  
            }  
            if (iter.hasNext()) sb.append(",");  
        }  
        sb.append("}");  
        return sb;  
    }  
    /** 
     * 将list转为json 
     * @param lists 
     * @param sb 
     * @return 
     */  
    public static StringBuilder listToJson(List<?> lists, StringBuilder sb) {  
        if (sb == null) sb = new StringBuilder();  
        sb.append("[");  
        for (int i = 0; i < lists.size(); i++) {  
            Object o = lists.get(i);  
            if (o instanceof Map<?, ?>) {  
                Map<?, ?> map = (Map<?, ?>) o;  
                mapToJson(map, sb);  
            } else if (o instanceof List<?>) {  
                List<?> l = (List<?>) o;  
                listToJson(l, sb);  
            } else {  
                sb.append("\"" + o + "\"");  
            }  
            if (i != lists.size() - 1) sb.append(",");  
        }  
        sb.append("]");  
        return sb;  
    }  
    /** 
     * JsonArray转List 
     * @param jsonArr 
     * @return 
     * @throws JSONException 
     */  
    public static List<Object> jsonToList(JSONArray jsonArr)  
            throws JSONException {  
        List<Object> jsonToMapList = new ArrayList<Object>();  
        for (int i = 0; i < jsonArr.size(); i++) {  
            Object object = jsonArr.get(i);  
            if (object instanceof JSONArray) {  
                jsonToMapList.add(jsonToList((JSONArray) object));  
            } else {  
                jsonToMapList.add(object);  
            }  
        }  
        return jsonToMapList;  
    }  
    /** 
     * 将字符串转为json数据 
     * @param str 数据字符串 
     * @return json字符串 
     */  
    private static String stringToJson(String str) {    
        StringBuffer sb = new StringBuffer();    
        for (int i = 0; i < str.length(); i++) {    
            char c = str.charAt(i);    
            switch (c) {    
                case '\"':    
                    sb.append("\\\"");    
                    break;    
                case '\\':    
                    sb.append("\\\\");    
                    break;    
                case '/':    
                    sb.append("\\/");    
                    break;    
                case '\b':    
                    sb.append("\\b");    
                    break;    
                case '\f':    
                    sb.append("\\f");    
                    break;    
                case '\n':    
                    sb.append("\\n");    
                    break;    
                case '\r':    
                    sb.append("\\r");    
                    break;    
                case '\t':    
                    sb.append("\\t");    
                    break;    
                default:    
                    sb.append(c);    
            }    
        }    
        return sb.toString();    
    }

	public static String createLinkStringByGet(Map<String, String> params) throws UnsupportedEncodingException {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
//			value = URLEncoder.encode(value, "UTF-8");
			if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/**
	 * 移除map的空key
	 * 
	 * @param map
	 * @return
	 */
	public static void removeNullKey(Map map) {
		Set set = map.keySet();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Object obj = (Object) iterator.next();
			remove(obj, iterator);
		}
	}

	/**
	 * 移除map中的value空值
	 * 
	 * @param map
	 * @return
	 */
	public static void removeNullValue(Map map) {
		Set set = map.keySet();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Object obj = (Object) iterator.next();
			Object value = (Object) map.get(obj);
			remove(value, iterator);
		}
	}

	/**
	 * Iterator 是工作在一个独立的线程中，并且拥有一个 mutex 锁。 Iterator
	 * 被创建之后会建立一个指向原来对象的单链索引表，当原来的对象数量发生变化时，这个索引表的内容不会同步改变，
	 * 所以当索引指针往后移动的时候就找不到要迭代的对象，所以按照 fail-fast 原则 Iterator 会马上抛出
	 * java.util.ConcurrentModificationException 异常。 所以 Iterator
	 * 在工作的时候是不允许被迭代的对象被改变的。 但你可以使用 Iterator 本身的方法 remove() 来删除对象，
	 * Iterator.remove() 方法会在删除当前迭代对象的同时维护索引的一致性。
	 * 
	 * @param obj
	 * @param iterator
	 */
	private static void remove(Object obj, Iterator iterator) {
		if (obj instanceof String) {
			String str = (String) obj;
			if (StringUtils.isEmpty(str)) {
				iterator.remove();
			}
		} else if (obj instanceof Collection) {
			Collection col = (Collection) obj;
			if (col == null || col.isEmpty()) {
				iterator.remove();
			}

		} else if (obj instanceof Map) {
			Map temp = (Map) obj;
			if (temp == null || temp.isEmpty()) {
				iterator.remove();
			}

		} else if (obj instanceof Object[]) {
			Object[] array = (Object[]) obj;
			if (array == null || array.length <= 0) {
				iterator.remove();
			}
		} else {
			if (obj == null) {
				iterator.remove();
			}
		}
	}
	
	public String onlineBankJson(String url, String map) {
//		String url = "http://127.0.0.1:9090/services/mainScan/onlineBank";
		System.out.println("请求=============="+map);
		String result = Sender.sendHttpReq(url, JSON.parseObject(map), "utf-8");
		System.out.println("result=============="+result);
		return result;
	}
}
