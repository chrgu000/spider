package com.dr.spider.service;

import com.dr.spider.base.SpiderInfo;
import com.dr.spider.model.BaseVideo;
import com.dr.spider.utils.MD5;
import com.dr.spider.utils.OkHttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class HegreService {


    public final static Logger logger = LoggerFactory.getLogger(HegreService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public Object crawler(SpiderInfo spiderInfo) {
        BaseVideo video;
        try {
            String html = new OkHttpUtils(spiderInfo.getUrl()).sendGet();
            Document doc = Jsoup.parse(html);
            Elements eles = doc.select(".video_list li");
            String videoDetailUrl, sn, title, coverImg;
            for (Element ele : eles) {
                videoDetailUrl = ele.select("a").first().attr("href");
                sn = MD5.encode(spiderInfo.getWebCode() + "_" + ele.id());
                title = ele.select("a").first().attr("title");
                coverImg = ele.select("img").first().attr("src");
                System.out.println(videoDetailUrl + "    " + title + "    " + coverImg);


                video = new BaseVideo();


            }
        } catch (Exception e) {

        }
        return mongoTemplate.findAll(BaseVideo.class);
    }
}