package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.dr.spider.constant.CrawlConst;
import com.dr.spider.constant.GlobalConst;
import com.dr.spider.utils.FileIOUtils;
import com.dr.spider.utils.MD5;

public class HegreCrawler extends BreadthCrawler {

  public static final String cookie = "_retina=0; _ga=GA1.2.411007182.1556615059; __auc=4beda20a16a6d7cd8ccac919d9e; stay-in-touch=0; _width=2560; __asc=84cae5f616a9bce0e206f87ccf8; _gid=GA1.2.127777201.1557392134; locale=en; login=ODljZjJkMGZjYzY1YWM2OWE4YzcwMDM4YjA4NGRmZDd8MTU1NzM5MjI0M3wzfDMwNDM3NnwyMDIuNDYuMzcuMTc2fDA=; _www.hegre.com_session=BAh7CDoPc2Vzc2lvbl9pZEkiJWI0YzQ4YTRmOTA5NTk0NDgzY2UyOWQ0OWQwZWM0MzM0BjoGRUY6EF9jc3JmX3Rva2VuSSIxeW5mblQ3R0FGczFHZE5vdUxzU3Q1eXA0Z0UvRWtDNjNYb2xsc0Yya0oyUT0GOwZGSSIKZmxhc2gGOwZUSUM6J0FjdGlvbkNvbnRyb2xsZXI6OkZsYXNoOjpGbGFzaEhhc2h7Bjogc2V0X2NvbGxlY3Rpb25fcG9wdXBfY29va2llVAY6CkB1c2VkewY7CUY%3D--fe2ac89ec34b53132c6bda23bedfebd5037b1616";

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
    if (page.code() == 301 || page.code() == 302) {
      crawlDatums.addAndReturn(page.location()).meta(page.meta());
      return;
    }
    String url = page.url();
    if (page.matchType("list")) {

      crawlDatums.add(page.links(".item>a")).type("content");
    } else if (page.matchType("content")) {
      String download = page.select(".resolution.content.top-resolution>a").first().attr("href");
      if ("http://cdn.content.hegre.com/films/ariel-photo-fantasy/ariel-photo-fantasy-2160p.mp4?download=true&v=1508193281"
          .equals(download)) {
        String videoPath = FileIOUtils
            .downloadVideo(download, MD5.encode(download), ".mp4", GlobalConst.GLOBAL_PATH, cookie);
        System.out.println(videoPath);
      }
      System.out.println(download);
    }

  }

  public static void main(String[] args) throws Exception {
    HegreCrawler crawler = new HegreCrawler(CrawlConst.CRAWL_PATH, false);
    crawler.getConf().setExecuteInterval(5000);
    crawler.getConf().set("title_prefix", "PREFIX_");
    crawler.getConf().set("content_length_limit", 20);
    crawler.getConf().setThreadKiller(1000 * 60 * 60 * 2);
    /*start crawl with depth of 4*/
    crawler.start(4);
  }
}
