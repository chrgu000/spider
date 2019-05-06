package com.dr.spider.parser;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;

public class DemoAutoNewsCrawler extends BreadthCrawler {

  public DemoAutoNewsCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
  }

  @Override
  public void visit(Page page, CrawlDatums crawlDatums) {


  }
}
