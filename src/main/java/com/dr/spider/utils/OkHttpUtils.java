package com.dr.spider.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

  public final static Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);

  private OkHttpClient okHttpClient;

  private OkHttpClient.Builder clientBuilder;

  private String url;

  private Request.Builder reqBuilder;

  public String getUrl() {
    return url;
  }

  public OkHttpUtils(String url) {
    this.url = url;
    clientBuilder = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS);
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

  public OkHttpUtils proxy(ProxyVo proxyVo) {
    if (proxyVo != null && StringUtils.isNotEmpty(proxyVo.getIp())) {
      Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyVo.getIp(), proxyVo.getPort()));
      clientBuilder.proxy(proxy);
      if (StringUtils.isNotEmpty(proxyVo.getUserName())) {
        // 使用okhttp的用户密码鉴权方式
        Authenticator proxyAuthenticator = new Authenticator() {
          @Override
          public Request authenticate(Route route, Response response) throws IOException {
            String credential = Credentials.basic(proxyVo.getUserName(), proxyVo.getPassWord());
            return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
          }
        };
        clientBuilder.authenticator(proxyAuthenticator);
      }
    }
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
    okHttpClient = clientBuilder.build();
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


