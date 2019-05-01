package com.dr.spider.test;

import com.dr.spider.utils.TusUtils;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

public class Tmp {

    public static void main(String[] args) throws Exception {
        String url = "http://upload289.fvs.io/upload/";
        File file = new File("/Users/longlongl/work/tt_bak/Downloads/ts/test.mp4");
        Map<String, String> metadata = new HashMap<>();
        metadata.put("token", "cEdFU1hLMG5YWURUdGVDUmYxcHg3SG5hM1hyd1BiVVdvcitNbXZLS0hNaXFKYUNuS1c5WFlpK1VnTm9DK1JxVkdxREY1REx0NC9wRm5hdjVPdz09OnFuMHFXMk9CeGhBdkprcVRmSElvWHc9PQ");
        metadata.put("name", file.getName());
        TusUtils.update(url, file, metadata);
    }
}
