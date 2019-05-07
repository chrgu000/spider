package com.dr.spider.model;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Field;

public class AvVideo extends VideoInfo {

    @Field("number")
    // 番号
    private String number;

    @Field("director")
    // 导演
    private String director;

    @Field("publisher")
    // 发行商
    private String publisher;

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

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
