package com.dr.spider.utils;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

  final static Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);

  private OkHttpClient okHttpClient;

  private String url;

  private Request.Builder reqBuilder;

  public String getUrl(){
    return url;
  }

  public OkHttpUtils(String url) {
    this.url=url;
    okHttpClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();
    reqBuilder = new Request.Builder().url(url);
  }


  public OkHttpUtils post(RequestBody body) {
//    FormBody body = new FormBody.Builder()
//        .add("key1", "value1")
//        .add("key2", "value2")
//        .add("key3", "value3")
//        .build();
    reqBuilder.post(body);
    return this;
  }

  public OkHttpUtils addCookie(String value) {
    reqBuilder.addHeader("Cookie", value);
    return this;
  }

  public OkHttpUtils addHeader(String key, String value) {
    reqBuilder.addHeader(key, value);
    return this;
  }


  public String send() {
    String result = "";
    try {
      Response response = response();
      result = response.body().string();
      // logger.debug("请求结果：{}", result);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public Response response() {
    Call call = okHttpClient.newCall(reqBuilder.build());
    Response response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }

}

enum Method {

  GET("get"), POST("post");

  private String value;

  Method(String value) {
    this.value = value;
  }

}
