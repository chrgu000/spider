package com.dr.spider.base;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class SpiderInfo {

  private Integer pageNO = 1;

  private Integer pageSize;

  private Integer webCode;

  private String url;

  private String globalPath;

  private String webCookie;

  public Integer getPageNO() {
    return pageNO;
  }

  public void setPageNO(Integer pageNO) {
    this.pageNO = pageNO;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Integer getWebCode() {
    return webCode;
  }

  public void setWebCode(Integer webCode) {
    this.webCode = webCode;
  }

  public String getGlobalPath() {
    return globalPath;
  }

  public void setGlobalPath(String globalPath) {
    this.globalPath = globalPath;
  }

  public String getWebCookie() {
    return webCookie;
  }

  public void setWebCookie(String webCookie) {
    this.webCookie = webCookie;
  }

  public String getHost() {
    String host = "";
    try {
      if (StringUtils.isNotEmpty(this.url)) {
        if (url.startsWith(HTTP)) {
          host = HTTP + new URL(url).getHost();
        } else if (url.startsWith(HTTPS)) {
          host = HTTPS + new URL(url).getHost();
        }
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return host;
  }

  private static final String HTTP = "http://";

  private static final String HTTPS = "https://";

}
