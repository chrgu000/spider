package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.dr.spider.constant.CrawlConst;
import com.dr.spider.service.PopjavService;
import com.dr.spider.utils.OkHttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PopjavCrawler extends BreadthCrawler {



  public PopjavCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    // add 5 start pages and set their type to "list"
    //"list" is not a reserved word, you can use other string instead
    this.addSeedAndReturn("https://popjav.tv/").type("list");
    for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
      String seedUrl = String.format("https://popjav.tv/page/%d", pageIndex);
      this.addSeed(seedUrl, "list");
    }
    setThreads(50);
    getConf().setTopN(100);

    //enable resumable mode
    //setResumable(true);
  }

  @Override
  public void visit(Page page, CrawlDatums crawlDatums) {
    if (page.code() == 301 || page.code() == 302) {
      crawlDatums.addAndReturn(page.location()).meta(page.meta());
      return;
    }
    String url = page.url();
    if (page.matchType("list")) {
      /*if type is "list"*/
      /*detect content page by css selector and mark their types as "content"*/
      crawlDatums.add(page.links(".video>a")).type("content");
    } else if (page.matchType("content")) {

      String vid = page.select("#b_vidoza").first().attr("date");
      String play= PopjavService.videoUrlDecode(vid);
      System.out.println(play);
      //String downloadUrl= Jsoup.parse(new OkHttpUtils(play).sendGet()).select("#player source").first().attr("src");
      //System.out.println(downloadUrl);

    }
  }

  public static void main(String[] args) throws Exception {
    PopjavCrawler crawler = new PopjavCrawler(CrawlConst.CRAWL_PATH, false);
    crawler.getConf().setExecuteInterval(5000);
    crawler.getConf().set("title_prefix", "PREFIX_");
    crawler.getConf().set("content_length_limit", 20);
    /*start crawl with depth of 4*/
    crawler.start(4);
  }
}
