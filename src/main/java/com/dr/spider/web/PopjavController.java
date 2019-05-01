package com.dr.spider.web;

import com.dr.spider.base.BaseController;
import com.dr.spider.service.PopjavService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("av/popjav")
public class PopjavController extends BaseController {

    @Autowired
    private PopjavService popjavService;

    int pageNO = 1;
    int pageSize = 1;
    String url = "https://popjav.tv/page/*";
    int webcode = 1000;


    @RequestMapping(value = "crawler", method = RequestMethod.GET)
    public Object crawler() {
        for (int i = pageNO; i <= pageSize; i++) {
            url = url.replace("*", i + "");
            popjavService.crawler(url, webcode);
        }
        return "";
    }

}
