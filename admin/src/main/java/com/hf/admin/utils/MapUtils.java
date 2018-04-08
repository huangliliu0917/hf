package com.hf.admin.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tengfei on 2017/11/11.
 */
public class MapUtils {
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

    public static <K,V> Map<K,V> newMap() {
        return new HashMap<>();
    }
}
