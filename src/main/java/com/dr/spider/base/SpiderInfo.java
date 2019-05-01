package com.dr.spider.base;

public class SpiderInfo {

    private Integer pageNO=1;

    private Integer pageSize;

    private Integer webCode;

    private String url;


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
}
