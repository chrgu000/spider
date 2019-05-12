package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dr.spider.constant.CrawlConst;
import com.dr.spider.constant.GlobalConst;
import com.dr.spider.model.AvVideo;
import com.dr.spider.service.HegreService;
import com.dr.spider.utils.FileIOUtils;
import com.dr.spider.utils.JodaTimeUtils;
import com.dr.spider.utils.MD5;
import com.dr.spider.utils.OkHttpUtils;
import com.dr.spider.utils.helper.FembedHelper;
import com.dr.spider.utils.helper.FembedResponse;
import com.dr.spider.utils.helper.MongodbHelper;
import com.mongodb.BasicDBObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScuteCrawler extends BreadthCrawler {

  public final static Logger logger = LoggerFactory.getLogger(ScuteCrawler.class);

  public static final String COOKIE = "_retina=0; _ga=GA1.2.411007182.1556615059; __auc=4beda20a16a6d7cd8ccac919d9e; locale=en; _www.hegre.com_session=BAh7CDoPc2Vzc2lvbl9pZEkiJWI0YzQ4YTRmOTA5NTk0NDgzY2UyOWQ0OWQwZWM0MzM0BjoGRUY6EF9jc3JmX3Rva2VuSSIxeW5mblQ3R0FGczFHZE5vdUxzU3Q1eXA0Z0UvRWtDNjNYb2xsc0Yya0oyUT0GOwZGSSIKZmxhc2gGOwZUSUM6J0FjdGlvbkNvbnRyb2xsZXI6OkZsYXNoOjpGbGFzaEhhc2h7AAY6CkB1c2VkewA%3D--068506f4ef97c2ac9971bb65b3bf55e67c63ef62; _width=2560; __asc=a47bba1916aac0835baeaadc849; _gid=GA1.2.1863182970.1557664382; _gat_betaVisitorsTracker=1; _gat_betaNonMembersTracker=1; elogin=YTQwNzc4ZWExNjYyOTA3NWQwYzk0NTBlNWU3ZTkyMTV8MTU1NzY2NDQyMXwzfDMwNDM3NnwxMjcuMC4wLjF8MA%3D%3D";
  public static final int WEBCODE = 1000;
  public static final String WEBURL = "http://www.s-cute.com/";

  private static final String API_1 = "https://d39pi0lkkywza9.cloudfront.net/hls/videos/VIDEOSID.json";
  private static final String API_2 = "https://d39pi0lkkywza9.cloudfront.net/hls/videos/VIDEOSID.m3u8?token=";

  public ScuteCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.addSeedAndReturn("http://www.s-cute.com/contents/").type("list");
    for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
      String seedUrl = String.format("http://www.s-cute.com/contents/?&page=%d", pageIndex);
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
        crawlDatums.add(page.links(".contents>a")).type("content");
      } else if (page.matchType("content")) {
        String a = page.url();
        String sn = MD5.encode(a);

        Document viDoc = MongodbHelper
            .findOne(new BasicDBObject("sn", new BasicDBObject("$eq", sn)),
                GlobalConst.COLLECTION_NAME_VIDEOINFO);
        if (viDoc == null) {

          String videoPath = download(a, sn);
          if (StringUtils.isEmpty(videoPath)) {
            logger.error("{} 没有获取到视频下载地址：{}", WEBURL, a);
            return;
          }
          // 标题
          String title = page.select(".h1").html();
          // 发行时间
          String publisherDate = page.select(".date").html().split(" ")[1];
          // 标签
          StringBuilder tag = new StringBuilder();
          for (Element ele : page.select(".tags>a")) {
            tag.append(ele.html()).append(",");
          }
          if (tag.length() > 0) {
            tag.delete(tag.lastIndexOf(","), tag.length());
          }
          // 封面
          String coverImg = page.select(".content-cover>img").first().attr("src");
          System.out.println(title + "    " + publisherDate + "    " + tag + "" + coverImg);
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
              JodaTimeUtils.formatToDate(publisherDate, JodaTimeUtils.DATE_FORMAT_YYYYMMDD_01));
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String download(String a, String sn) {
    String fileName = "";
    try {
      String videoId = a.split("/")[4];
      String json = new OkHttpUtils(API_1.replace("VIDEOSID", videoId)).addReferer(a).send();
      JSONObject tokenObj = JSON.parseObject(json);
      String token = tokenObj.getString("token");
      String m3u8Url = getM3u8Url(
          new OkHttpUtils(API_2.replace("VIDEOSID", videoId) + token).addReferer(a).response()
              .body().byteStream());
      List<String> tsList = FileIOUtils.getTsList(a, m3u8Url);
      fileName = FileIOUtils.downloadM3u8(GlobalConst.GLOBAL_PATH, sn, tsList);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileName;
  }

  private static String getM3u8Url(InputStream is) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String line;
    List<String> m3u8sList = new ArrayList<>();
    while ((line = br.readLine()) != null) {
      if (!line.contains("#")) {
        m3u8sList.add(line);
      }
    }
    if (m3u8sList.size() == 0) {
      return "";
    }
    return m3u8sList.get(m3u8sList.size() - 1).replaceAll("amp;", "");
  }


  public static void main(String[] args) throws Exception {
    ScuteCrawler crawler = new ScuteCrawler(CrawlConst.CRAWL_PATH, false);
    crawler.getConf().setExecuteInterval(5000);
    crawler.getConf().set("title_prefix", "PREFIX_");
    crawler.getConf().set("content_length_limit", 20);
    crawler.getConf().setThreadKiller(1000 * 60 * 60 * 24);
    /*start crawl with depth of 4*/
    crawler.start(4);
  }
}
