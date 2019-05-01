package com.dr.spider.web;

import com.dr.spider.base.BaseController;
import com.dr.spider.base.SpiderInfo;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("av/hegre")
public class HegreController extends BaseController {


    @ModelAttribute
    public void init(){
        spiderInfo=new SpiderInfo();
        spiderInfo.setUrl("https://www.hegre.com");
        spiderInfo.setWebCode(1000);
    }


    @RequestMapping(value = "crawler", method = RequestMethod.GET)
    public Object crawler() {



        return "";
    }

}
