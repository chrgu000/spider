package com.dr.spider.utils.helper;

public class FembedResponse {

  private boolean success;

  /**
   * 视频上传地址
   */
  private String url;

  /**
   * 视频上传凭证
   */
  private String token;

  /**
   * 视频地址
   */
  private String videoUrl;

  /**
   * 视频ID
   */
  private String videoId;

  /**
   * 文件指纹
   */
  private String fingerprint;


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getVideoId() {
    return videoId;
  }

  public void setVideoId(String videoId) {
    this.videoId = videoId;
  }

  public String getFingerprint() {
    return fingerprint;
  }

  public void setFingerprint(String fingerprint) {
    this.fingerprint = fingerprint;
  }
}
