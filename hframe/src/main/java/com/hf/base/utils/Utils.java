package com.hf.base.utils;

import com.hf.base.exceptions.BizFailException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tengfei on 2017/10/28.
 */
public class Utils {

    public static Map<String,Object> bean2Map(Object obj) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor:propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(obj, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } else {
                        returnMap.put(propertyName, "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMap;
    }

    public static boolean checkSign(Object obj,String field) {
        Map<String,Object> map = bean2Map(obj);
        Map<String,Object> treeMap = new TreeMap<>((o1, o2) -> o1.compareTo(o2));

        for(String key:map.keySet()) {
            if(StringUtils.equals(key,field)) {
                continue;
            }

            if(StringUtils.isEmpty(String.valueOf(map.get(key)))) {
                continue;
            }

            treeMap.put(key,map.get(key));
        }

        StringBuilder sb = new StringBuilder("");
        for(String key:treeMap.keySet()) {
            sb.append("&").append(key).append("=").append(treeMap.get(key));
        }

        return StringUtils.equals(String.valueOf(map.get(field)),sb.toString());
    }

    public static <K,V> Map<K,V> buildMap(Object... objs) {
        Map<K,V> map = new HashMap<>();
        append(map,objs);
        return map;
    }

    public static <K,V> void append(Map<K,V> map,Object... objs) {

        int z = objs.length;
        for(int i = 0; i < z - 1; i += 2) {
            map.put((K)objs[i], (V)objs[i + 1]);
        }
    }

    public static String convertPassword(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String getUrlParam(Object obj,Class dataType) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(dataType);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            StringBuilder params = new StringBuilder("");

            for(PropertyDescriptor descriptor : descriptors) {
                if(StringUtils.equalsIgnoreCase("class",descriptor.getDisplayName())) {
                    continue;
                }
                Object o = descriptor.getReadMethod().invoke(obj);
                if(Objects.isNull(o)) {
                    continue;
                }
                params.append(String.format("%s=%s&",descriptor.getDisplayName(),String.valueOf(o)));
            }

            return params.substring(0,params.length()-1);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    public static boolean checkEncrypt(Map<String,Object> map,String cipherCode) {
        String sign = map.get("sign").toString();
        Set<String> set = map.keySet().parallelStream().collect(Collectors.toCollection(TreeSet::new));
        StringBuilder str = new StringBuilder("");
        for(String key:set) {
            if(StringUtils.equalsIgnoreCase("sign",key)) {
                continue;
            }
            if(Objects.isNull(map.get(key)) || Utils.isEmpty(String.valueOf(map.get(key)))) {
                continue;
            }
            str = str.append(String.format("%s=%s",key,map.get(key)));
            str = str.append("&");
        }
        str = str.append("key=").append(cipherCode);
        return StringUtils.equals(DigestUtils.md5Hex(str.toString()).toUpperCase(),sign);
    }

    public static String encrypt(Map<String,Object> map,String cipherCode) {
        Set<String> set = map.keySet().parallelStream().collect(Collectors.toCollection(TreeSet::new));
        StringBuilder str = new StringBuilder("");
        for(String key:set) {
            if(StringUtils.equalsIgnoreCase("sign",key)) {
                continue;
            }
            if(Objects.isNull(map.get(key)) || Utils.isEmpty(String.valueOf(map.get(key)))) {
                continue;
            }
            str = str.append(String.format("%s=%s",key,map.get(key)));
            str = str.append("&");
        }
        str = str.append("key=").append(cipherCode);
        return DigestUtils.md5Hex(str.toString()).toUpperCase();
    }

    public static String getEncryptStr(Map<String,Object> map) {
        Set<String> set = map.keySet().parallelStream().collect(Collectors.toCollection(TreeSet::new));
        StringBuilder str = new StringBuilder("");
        for(String key:set) {
            if(StringUtils.equalsIgnoreCase("sign",key)) {
                continue;
            }
            if(Objects.isNull(map.get(key)) || Utils.isEmpty(String.valueOf(map.get(key)))) {
                continue;
            }
            str = str.append(String.format("%s=%s",key,map.get(key)));
            str = str.append("&");
        }
        return str.substring(0,str.length()-1);
    }

    public static String encrypt2(Map<String,Object> map,String cipherCode) {
        Set<String> set = map.keySet().parallelStream().collect(Collectors.toCollection(TreeSet::new));
        StringBuilder str = new StringBuilder("");
        for(String key:set) {
            if(StringUtils.equalsIgnoreCase("sign",key)) {
                continue;
            }
            str = str.append(String.format("%s=%s",key,map.get(key)));
            str = str.append("&");
        }
        String params =str.substring(0,str.length()-1);
        params = params+cipherCode;
        return DigestUtils.md5Hex(params);
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str) || StringUtils.equalsIgnoreCase("null",str);
    }

    public static String getMchId() {
        return null;
    }

    public static String nvl(Object obj) {
        return null == obj?"":String.valueOf(obj);
    }
}
