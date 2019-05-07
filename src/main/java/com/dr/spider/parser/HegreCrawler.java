package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.dr.spider.constant.CrawlConst;
import com.dr.spider.service.PopjavService;
import org.jsoup.select.Elements;

public class HegreCrawler extends BreadthCrawler {

  public static final String cookie = "_retina=0; _ga=GA1.2.411007182.1556615059; __auc=4beda20a16a6d7cd8ccac919d9e; stay-in-touch=0; _width=2560; _gid=GA1.2.1070355812.1557214646; hide-online-cam-performers=1557236303289; __asc=b278d08e16a91830b1b704d7bd3; login=MzcyNDQ3MWY1MmY1YzQ3YjVmNzZmYzRmMWQ2YzVjNDJ8MTU1NzIyMDA1MXwzfDMwNDM3NnwxNDYuMTk2LjkxLjEzMHww; _www.hegre.com_session=BAh7CToPc2Vzc2lvbl9pZEkiJWVjODM1MWYzMDliN2ZmYTBmYjAxZWIxNmNjZTc2NTFjBjoGRUY6EF9jc3JmX3Rva2VuSSIxS1R2S0t3M0VQaVo4N2NqQWpvZ2hvSk5FdHhaaHIzVzR4VWNwL2UzMlJYMD0GOwZGSSIKZmxhc2gGOwZUSUM6J0FjdGlvbkNvbnRyb2xsZXI6OkZsYXNoOjpGbGFzaEhhc2h7AAY6CkB1c2VkewA6FWJhY2tncm91bmRfY292ZXJJIhRwcm9maWxlLWNvdmVyLTYGOwZU--65c238a4094f00309d683f1341f837835ba4ae3e";

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
      String download= page.select(".resolution.content.top-resolution>a").first().attr("href");


      System.out.println(download);
    }

  }

  public static void main(String[] args) throws Exception {
    HegreCrawler crawler = new HegreCrawler(CrawlConst.CRAWL_PATH, false);
    crawler.getConf().setExecuteInterval(5000);
    crawler.getConf().set("title_prefix", "PREFIX_");
    crawler.getConf().set("content_length_limit", 20);
    /*start crawl with depth of 4*/
    crawler.start(4);
  }
}
