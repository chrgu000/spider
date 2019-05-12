package com.dr.spider.utils.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dr.spider.utils.FileIOUtils;
import com.dr.spider.utils.OkHttpUtils;
import com.dr.spider.utils.TusUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
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

  public static FembedResponse fingerprint(FembedResponse res) {
    FormBody body = new FormBody.Builder().add("client_id", API_ID).add("client_secret", API_SECRET)
        .add("file_fingerprint", res.getFingerprint()).build();
    String context = new OkHttpUtils("https://www.fembed.com/api/fingerprint").post(body).send();
    JSONObject obj = JSON.parseObject(context);
    res.setSuccess(obj.getBoolean("success"));
    if (obj.getBoolean("success")) {
      res.setVideoId(obj.getString("data"));
    } else {
      logger.error("获取视频ID失败：{}", res.getVideoUrl());
    }
    return res;
  }

  /**
   * 上传视频封面
   */
  public static String poster(FembedResponse res, String imgPath) {
    JSONObject poster = new JSONObject();
    poster.put("type", "png");
    poster.put("content", FileIOUtils.fileToBase64(imgPath));
    String file_id = res.getVideoId();
    FormBody body = new FormBody.Builder().add("client_id", API_ID).add("client_secret", API_SECRET)
        .add("file_id", res.getVideoId()).add("poster", poster.toString()).build();
    String context = new OkHttpUtils("https://www.fembed.com/api/poster").post(body).send();
    return context;

  }

  public static FembedResponse fembedVideoUpload(String filePath, String folder_id) {
    FembedResponse res = null;
    try {
      if (StringUtils.isNotEmpty(folder_id)) {
        res = upload(folder_id);
      } else {
        res = upload();
      }
      File file = new File(filePath);
      Map<String, String> metadata = new HashMap<>();
      metadata.put("token", res.getToken());
      metadata.put("name", file.getName());
      String videoUrl = TusUtils.update(res.getUrl(), file, metadata);
      res.setVideoUrl(videoUrl);
      res.setFingerprint(videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length()));
      res = fingerprint(res);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return res;
  }

  public static void main(String[] args) {
    FembedResponse res = new FembedResponse();
    res.setFingerprint("887d9100d5e1df34faa6c225ae595574");
    res = fingerprint(res);
    System.out.println(res.getVideoId());
  }
}
