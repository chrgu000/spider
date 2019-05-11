package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.alibaba.fastjson.JSON;
import com.dr.spider.constant.CrawlConst;
import com.dr.spider.constant.GlobalConst;
import com.dr.spider.model.VideoInfo;
import com.dr.spider.utils.FileIOUtils;
import com.dr.spider.utils.MD5;
import com.dr.spider.utils.OkHttpUtils;
import com.dr.spider.utils.helper.FembedHelper;
import com.dr.spider.utils.helper.MongodbHelper;
import com.dr.spider.utils.helper.ProxyVoHelper;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class HegreCrawler extends BreadthCrawler {

  public static final String cookie = "_retina=0; _ga=GA1.2.411007182.1556615059; __auc=4beda20a16a6d7cd8ccac919d9e; stay-in-touch=0; _width=2560; locale=en; __asc=49abbdac16aa4db68c3a18f97f7; _gid=GA1.2.1453415963.1557544004; _gat_betaVisitorsTracker=1; _gat_betaNonMembersTracker=1; login=M2QxOTlkYTQwMzUxNDNiNDNjNTg3ODliZWY1ODBiODR8MTU1NzU0NDA0N3wzfDMwNDM3NnwyMDIuNDYuMzcuMTc2fDA=; _www.hegre.com_session=BAh7CDoPc2Vzc2lvbl9pZEkiJWI0YzQ4YTRmOTA5NTk0NDgzY2UyOWQ0OWQwZWM0MzM0BjoGRUY6EF9jc3JmX3Rva2VuSSIxeW5mblQ3R0FGczFHZE5vdUxzU3Q1eXA0Z0UvRWtDNjNYb2xsc0Yya0oyUT0GOwZGSSIKZmxhc2gGOwZUSUM6J0FjdGlvbkNvbnRyb2xsZXI6OkZsYXNoOjpGbGFzaEhhc2h7Bjogc2V0X2NvbGxlY3Rpb25fcG9wdXBfY29va2llVAY6CkB1c2VkewY7CUY%3D--fe2ac89ec34b53132c6bda23bedfebd5037b1616";

  public HegreCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.addSeedAndReturn("https://www.hegre.com/films").type("list");
    for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
      String seedUrl = String.format("https://www.hegre.com/films?films_page=%d", pageIndex);
      this.addSeed(seedUrl, "list");
    }

    setThreads(2);
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
                  cookie);
          if (StringUtils.isNotEmpty(videoPath)) {
            VideoInfo v = new VideoInfo();
            v.setSn(sn);
            Document vDoc = Document.parse(JSON.toJSONString(v));
            MongodbHelper.insert(vDoc, GlobalConst.COLLECTION_NAME_VIDEOINFO);
            String result = FembedHelper.fembedVideoUpload(videoPath);
            System.out.println("上传结果: " + result);
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
