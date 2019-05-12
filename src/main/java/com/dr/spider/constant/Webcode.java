package com.dr.spider.constant;

public enum Webcode {

  hegre(1000);

  private int code;

  private Webcode(int code) {
    this.code = code;
  }


  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
