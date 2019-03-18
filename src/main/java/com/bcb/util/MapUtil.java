package com.bcb.util;

import com.bcb.bean.JSONParam;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kx on 2018/1/16.
 */
public class MapUtil {
    public static Map<String, String> convertToMap(JSONParam[] params) {
        Map<String, String> map = new HashMap<>();
        if (params != null && params.length > 0) {
            for (JSONParam jsonParam : params) {
                map.put(jsonParam.getName(), jsonParam.getValue());
            }
        }
        return map;
    }
}
