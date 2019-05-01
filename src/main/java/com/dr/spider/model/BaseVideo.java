package com.dr.spider.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "BaseVideo")
public class BaseVideo {

    @Id
    // 主键
    private String id;

    @Field("status")
    // 状态 0:没有
    private Integer status;

    @Field("webCode")
    // 网站唯一标识
    private Integer webCode;

    @Field("title")
    // 标题
    private String title;

    @Field("actors")
    // 演员
    private String actors;

    @Field("publisherDate")
    // 出版时间
    private Date publisherDate;

    @Field("tags")
    // 标签
    private String tags;

    @Field("state")
    // 国家
    private String state;

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

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public Date getPublisherDate() {
        return publisherDate;
    }

    public void setPublisherDate(Date publisherDate) {
        this.publisherDate = publisherDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
