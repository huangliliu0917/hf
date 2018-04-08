package com.hf.base.utils;

import com.hf.base.exceptions.BizFailException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.StringUtils;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tengfei on 2017/11/4.
 */
public class TypeConverter {

    private static Map<Class,Map<String,PropertyDescriptor>> beanCache = new ConcurrentHashMap<>();

    public static <T> T convert(Map<String,Object> request,Class<T> dataType) {
        try {
            T data = dataType.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(dataType);

            if(MapUtils.isEmpty(beanCache.get(dataType))) {
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                Map<String,PropertyDescriptor> map = new HashMap<>();
                for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    map.put(propertyDescriptor.getName(),propertyDescriptor);
                }
                beanCache.put(dataType,map);
            }

            Field[] fields = dataType.getDeclaredFields();

            Map<String,List<String>> groupFieldMap = new HashMap<>();

            for(Field field:fields) {
                if(!field.isAnnotationPresent(com.hf.base.annotations.Field.class)) {
                    continue;
                }

                com.hf.base.annotations.Field fieldValue = field.getDeclaredAnnotation(com.hf.base.annotations.Field.class);

                String fieldStr = String.valueOf(request.get(StringUtils.isEmpty(fieldValue.alias())?field.getName():fieldValue.alias()));
                String value = (StringUtils.isEmpty(fieldStr) || org.apache.commons.lang3.StringUtils.equalsIgnoreCase("null",fieldStr)) ?fieldValue.defaults():fieldStr;

                if(org.apache.commons.lang3.StringUtils.isEmpty(value) || org.apache.commons.lang3.StringUtils.equalsIgnoreCase("null",value)) {
                    if(fieldValue.required() && !StringUtils.isEmpty(fieldValue.group())) {
                        throw new BizFailException("field empty");
                    }
                    continue;
                }

                if(!StringUtils.isEmpty(fieldValue.group())) {
                    if(CollectionUtils.isEmpty(groupFieldMap.get(fieldValue.group()))) {
                        groupFieldMap.put(fieldValue.group(),new ArrayList<>());
                    }
                    if(!Utils.isEmpty(value)) {
                        groupFieldMap.get(field.getName()).add(value);
                    }
                }

                if(fieldValue.required() && org.apache.commons.lang3.StringUtils.isEmpty(value)) {
                    throw new BizFailException(String.format("%s cannot be empty",field.getName()));
                } else if(Long.class.isAssignableFrom(field.getType())) {
                    beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,new BigDecimal(value).longValue());
                }else if(Integer.class.isAssignableFrom(field.getType())) {
                    beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,new BigDecimal(value).intValue());
                }else if(Date.class.isAssignableFrom(field.getType())) {
                    LocalDateTime localDateTime = LocalDateTime.parse(value);
                    ZoneId zone = ZoneId.systemDefault();
                    Instant instant = localDateTime.atZone(zone).toInstant();
                    Date date = Date.from(instant);
                    beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,date);
                } else if(BigDecimal.class.isAssignableFrom(field.getType())){
                    beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,new BigDecimal(value));
                } else {
                    beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,value);
                }
            }

            for(String key:groupFieldMap.keySet()) {
                if(CollectionUtils.isEmpty(groupFieldMap.get(key))) {
                    throw new BizFailException(String.format("all field in group empty,%s",key));
                }
            }

            return data;
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }
}
