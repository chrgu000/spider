package com.dr.spider.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "VideoInfo")
public class VideoInfo {
  @Id
  // 主键
  private String id;

  @Field("sn")
  // 唯一标识
  private String sn;

  @Field("webCode")
  // 网站唯一标识
  private Integer webCode;

  @Field("webUrl")
  private String webUrl;

  @Field("playUrl")
  private String playUrl;

  @Field("videoId")
  private String videoId;

  @Field("title")
  // 标题
  private String title;

  @Field("status")
  // 状态 0:没有
  private Integer status=1;

  @Field("insertDate")
  // 插入时间
  private Date insertDate;

  //封面原地址
  @Field("coverImg")
  private String coverImg;

  //封面本地地址
  @Field("coverImgLocal")
  private String coverImgLoacl;

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getWebCode() {
    return webCode;
  }

  public void setWebCode(Integer webCode) {
    this.webCode = webCode;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public String getPlayUrl() {
    return playUrl;
  }

  public void setPlayUrl(String playUrl) {
    this.playUrl = playUrl;
  }

  public Date getInsertDate() {
    return insertDate;
  }

  public void setInsertDate(Date insertDate) {
    this.insertDate = insertDate;
  }

  public String getVideoId() {
    return videoId;
  }

  public void setVideoId(String videoId) {
    this.videoId = videoId;
  }

  public String getCoverImg() {
    return coverImg;
  }

  public void setCoverImg(String coverImg) {
    this.coverImg = coverImg;
  }


  public String getCoverImgLoacl() {
    return coverImgLoacl;
  }

  public void setCoverImgLoacl(String coverImgLoacl) {
    this.coverImgLoacl = coverImgLoacl;
  }
}
