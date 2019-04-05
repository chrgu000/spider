package com.dr.spider.utils;

import com.alibaba.fastjson.JSON;

public class JsonUtils {

    public static boolean isJson(String str) {
        boolean result;
        try {
            Object obj = JSON.parse(str);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

}
