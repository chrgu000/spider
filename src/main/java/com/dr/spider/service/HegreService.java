package com.dr.spider.service;

import com.dr.spider.base.SpiderInfo;
import com.dr.spider.model.VideoInfo;
import com.dr.spider.utils.OkHttpUtils;
import java.util.List;
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
    List list = null;
    VideoInfo video = new VideoInfo();
    try {
      String html = new OkHttpUtils(spiderInfo.getUrl()).addCookie(spiderInfo.getWebCookie())
          .sendGet();
      Document doc = Jsoup.parse(html);
      Elements eles = doc.select(".item");
      String videoDetailUrl, sn, title, coverImg;
      for (Element ele : eles) {
        videoDetailUrl = spiderInfo.getHost() + ele.select("a").first().attr("href");




        System.out.println(videoDetailUrl);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // list = mongoTemplate.findAll(BaseVideo.class);
    return list;
  }

  public static void main(String[] args) {
    SpiderInfo si = new SpiderInfo();
    si.setUrl("https://www.hegre.com/films?films_page=1");
    si.setWebCookie(
        "_retina=0; _width=2560; _ga=GA1.2.411007182.1556615059; _gid=GA1.2.212494572.1556615059; __auc=4beda20a16a6d7cd8ccac919d9e; _www.hegre.com_session=BAh7CToPc2Vzc2lvbl9pZEkiJWVjODM1MWYzMDliN2ZmYTBmYjAxZWIxNmNjZTc2NTFjBjoGRUY6EF9jc3JmX3Rva2VuSSIxS1R2S0t3M0VQaVo4N2NqQWpvZ2hvSk5FdHhaaHIzVzR4VWNwL2UzMlJYMD0GOwZGSSIKZmxhc2gGOwZUSUM6J0FjdGlvbkNvbnRyb2xsZXI6OkZsYXNoOjpGbGFzaEhhc2h7AAY6CkB1c2VkewA6FWJhY2tncm91bmRfY292ZXJJIhRwcm9maWxlLWNvdmVyLTYGOwZU--65c238a4094f00309d683f1341f837835ba4ae3e; stay-in-touch=0");
    si.setWebCode(1000);
    HegreService service = new HegreService();
    service.crawler(si);
  }
}
