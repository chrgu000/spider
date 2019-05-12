package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.alibaba.fastjson.JSON;
import com.dr.spider.constant.CrawlConst;
import com.dr.spider.constant.GlobalConst;
import com.dr.spider.model.AvVideo;
import com.dr.spider.utils.JodaTimeUtils;
import com.dr.spider.utils.FileIOUtils;
import com.dr.spider.utils.MD5;
import com.dr.spider.utils.helper.FembedHelper;
import com.dr.spider.utils.helper.FembedResponse;
import com.dr.spider.utils.helper.MongodbHelper;
import com.mongodb.BasicDBObject;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jsoup.nodes.Element;

public class HegreCrawler extends BreadthCrawler {

  public static final String COOKIE = "_retina=0; _ga=GA1.2.411007182.1556615059; __auc=4beda20a16a6d7cd8ccac919d9e; locale=en; _www.hegre.com_session=BAh7CDoPc2Vzc2lvbl9pZEkiJWI0YzQ4YTRmOTA5NTk0NDgzY2UyOWQ0OWQwZWM0MzM0BjoGRUY6EF9jc3JmX3Rva2VuSSIxeW5mblQ3R0FGczFHZE5vdUxzU3Q1eXA0Z0UvRWtDNjNYb2xsc0Yya0oyUT0GOwZGSSIKZmxhc2gGOwZUSUM6J0FjdGlvbkNvbnRyb2xsZXI6OkZsYXNoOjpGbGFzaEhhc2h7AAY6CkB1c2VkewA%3D--068506f4ef97c2ac9971bb65b3bf55e67c63ef62; _width=2560; __asc=a47bba1916aac0835baeaadc849; _gid=GA1.2.1863182970.1557664382; _gat_betaVisitorsTracker=1; _gat_betaNonMembersTracker=1; elogin=YTQwNzc4ZWExNjYyOTA3NWQwYzk0NTBlNWU3ZTkyMTV8MTU1NzY2NDQyMXwzfDMwNDM3NnwxMjcuMC4wLjF8MA%3D%3D";
  public static final int WEBCODE = 1000;

  public HegreCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.addSeedAndReturn("https://www.hegre.com/films").type("list");
    for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
      String seedUrl = String.format("https://www.hegre.com/films?films_page=%d", pageIndex);
      this.addSeed(seedUrl, "list");
    }
    setThreads(50);
    getConf().setTopN(100);

  }

  @Override
  public void visit(Page page, CrawlDatums crawlDatums) {
    try {
      if (page.code() == 301 || page.code() == 302) {
        crawlDatums.addAndReturn(page.location()).meta(page.meta());
        return;
      }
      if (page.matchType("list")) {
        crawlDatums.add(page.links(".item>a")).type("content");
      } else if (page.matchType("content")) {
        String download = page.select(".resolution.content.top-resolution>a").first().attr("href");
        String sn = MD5.encode(download);
        Document viDoc = MongodbHelper
            .findOne(new BasicDBObject("sn", new BasicDBObject("$eq", sn)),
                GlobalConst.COLLECTION_NAME_VIDEOINFO);
        if (viDoc == null) {
          String videoPath = FileIOUtils
              .downloadVideo(download, MD5.encode(download), ".mp4", GlobalConst.GLOBAL_PATH,
                  COOKIE);
          if (StringUtils.isNotEmpty(videoPath)) {
            String title = page.select(".record-toolbar.clearfix>h1").html();
            String publisherDate = page.select(".date").html();

            StringBuilder tag = new StringBuilder();
            for (Element ele : page.select(".approved-tags>a")) {
              tag.append(ele.html()).append(",");
            }
            if (tag.length() > 0) {
              tag.delete(tag.lastIndexOf(","), tag.length());
            }
            String coverImg = page.select(".cover-links a").get(1).attr("href");
            // 封面图片下载到服务器
            String coverImgLocal = FileIOUtils.downloadImg(coverImg, sn, GlobalConst.GLOBAL_PATH);
            // 视频上传
            FembedResponse res = FembedHelper.fembedVideoUpload(videoPath, WEBCODE + "");
            System.out.println("上传结果: " + JSON.toJSONString(res));

            AvVideo v = new AvVideo();
            v.setSn(sn);
            v.setStatus(1);
            v.setWebCode(WEBCODE);
            v.setWebUrl(page.url());
            v.setTitle(title);
            v.setInsertDate(new Date());
            v.setPublisherDate(
                JodaTimeUtils.formatToDate(publisherDate, JodaTimeUtils.DATE_FORMAT_MMM_D_YYYY,
                    Locale.ENGLISH));
            v.setPlayUrl(res.getVideoUrl());
            v.setVideoId(res.getVideoId());
            v.setCoverImg(coverImg);
            v.setCoverImgLoacl(coverImgLocal);
            v.setTags(tag.toString());
            Document vDoc = Document
                .parse(JSON.toJSONStringWithDateFormat(v, "yyyy-MM-dd HH:mm:ss"));
            // 视频信息入库
            MongodbHelper.insert(vDoc, GlobalConst.COLLECTION_NAME_VIDEOINFO);
            // 上传后删除文件
            FileIOUtils.deleteFile(videoPath);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) throws Exception {
    HegreCrawler crawler = new HegreCrawler(CrawlConst.CRAWL_PATH, false);
    crawler.getConf().setExecuteInterval(5000);
    crawler.getConf().set("title_prefix", "PREFIX_");
    crawler.getConf().set("content_length_limit", 20);
    crawler.getConf().setThreadKiller(1000 * 60 * 60 * 24);
    /*start crawl with depth of 4*/
    crawler.start(4);
  }
}
