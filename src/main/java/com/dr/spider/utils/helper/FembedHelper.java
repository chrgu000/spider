package com.dr.spider.utils.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dr.spider.utils.OkHttpUtils;
import okhttp3.FormBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FembedHelper {


  public final static Logger logger = LoggerFactory.getLogger(FembedHelper.class);

  public final static String API_ID = "235999";
  public final static String API_SECRET = "dcf2572f5ac674f8b2d9e9454342cc451002";


  public static FembedResponse upload() {
    return upload(null);
  }

  public static FembedResponse upload(String folder_id) {
    FormBody body;
    if (StringUtils.isNotEmpty(folder_id)) {
      body = new FormBody.Builder().add("client_id", API_ID).add("client_secret", API_SECRET)
          .add("folder_id", folder_id).build();
    } else {
      body = new FormBody.Builder().add("client_id", API_ID).add("client_secret", API_SECRET)
          .build();
    }
    String context = new OkHttpUtils("https://www.fembed.com/api/upload").post(
        body)
        .send();
    JSONObject obj = JSON.parseObject(context);
    FembedResponse res = new FembedResponse();
    res.setSuccess(obj.getBoolean("success"));
    if (obj.getBoolean("success")) {
      res.setUrl(obj.getJSONObject("data").getString("url"));
      res.setToken(obj.getJSONObject("data").getString("token"));
    }
    return res;

  }


  public static void main(String[] args) {
    FembedResponse res = upload("1000");
    if (res.isSuccess()) {
      System.out.println(res.getUrl());
      System.out.println(res.getToken());


    }
  }

//  curl -
//  X POST
//  https://www.fembed.com/api/upload
//      -d "client_id=ClIENT_ID&client_secret=ClIENT_SECRET"
//      -d "folder_id=FOLDER_ID"
//      -H "Content-Type: application/x-www-form-urlencoded"

}
