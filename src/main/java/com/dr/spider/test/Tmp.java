package com.dr.spider.test;

import com.dr.spider.utils.TusUtils;

import java.io.File;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class Tmp {

    public static void main2(String[] args) throws Exception {
        String url = "http://upload291.fvs.io/upload/";
        File file = new File("/Users/longlongl/work/tt_bak/Downloads/ts/test2.mp4");
        Map<String, String> metadata = new HashMap<>();
        metadata.put("token", "dytWbllXYUhJS1ZyMVdpZTFwQVhnUzRxdDVOU1l3bkZZS0F3cVhZOE1zdVJrM1E3NXFEK04xM1p1SU11d3habWh4a1J5LzV2UFdGY2w2Qk9zdz09Om9PMUs0TWpqeHJlb3FFa2VwZ2VKYnc9PQ");
        metadata.put("name", file.getName());
        TusUtils.update(url, file, metadata);
    }


    public static void main(String[] args) {
        Date date=new Date(System.currentTimeMillis());
        System.out.println(date.toString());
    }
}
