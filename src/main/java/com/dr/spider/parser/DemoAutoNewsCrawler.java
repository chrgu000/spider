package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import com.dr.spider.utils.OkHttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DemoAutoNewsCrawler extends BreadthCrawler {

  public static void main(String[] args) throws Exception {
    DemoAutoNewsCrawler crawler = new DemoAutoNewsCrawler("/Users/longlongl/crawl", true);
    /*start crawl with depth of 4*/
    crawler.start(4);
  }

  public DemoAutoNewsCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    /*start pages*/
    this.addSeed("https://blog.github.com/");
    for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
      String seedUrl = String.format("https://blog.github.com/page/%d/", pageIndex);
      this.addSeed(seedUrl);
    }

    /*fetch url like "https://blog.github.com/2018-07-13-graphql-for-octokit/" */
    this.addRegex("https://blog.github.com/[0-9]{4}-[0-9]{2}-[0-9]{2}-[^/]+/");
    /*do not fetch jpg|png|gif*/
    //this.addRegex("-.*\\.(jpg|png|gif).*");
    /*do not fetch url contains #*/
    //this.addRegex("-.*#.*");

    setThreads(50);
    getConf().setTopN(100);

    //enable resumable mode
    //setResumable(true);

  }

  @Override
  public void visit(Page page, CrawlDatums crawlDatums) {

    String url = page.url();
    /*if page is news page*/
    if (page.matchUrl("https://blog.github.com/[0-9]{4}-[0-9]{2}-[0-9]{2}[^/]+/")) {

      /*extract title and content of news by css selector*/
      String title = page.select("h1[class=lh-condensed]").first().text();
      String content = page.selectText("div.content.markdown-body");

      System.out.println("URL:\n" + url);
      System.out.println("title:\n" + title);
      System.out.println("content:\n" + content);

      /*If you want to add urls to crawl,add them to nextLink*/
      /*WebCollector automatically filters links that have been fetched before*/
            /*If autoParse is true and the link you add to nextLinks does not match the
              regex rules,the link will also been filtered.*/
      //next.add("http://xxxxxx.com");
    } else {
      if (page.code() == 301 || page.code() == 302) {
        crawlDatums.addAndReturn(page.location()).meta(page.meta());
        return;
      }

      Elements eles = page.select(".post-item__date.col-12.col-md-3");
      for (Element e : eles) {
        String href = e.select("a").attr("href");
        System.out.println(href);
        crawlDatums.addAndReturn(page.location()).meta(page.meta());
      }
    }
  }
}
