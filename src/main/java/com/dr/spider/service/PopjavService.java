package com.dr.spider.service;

import com.dr.spider.model.BaseVideo;
import com.dr.spider.utils.MD5;
import com.dr.spider.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


@Service
public class PopjavService {

    public final static Logger logger = LoggerFactory.getLogger(PopjavService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public Object crawler(String url, int webcode) {
        BaseVideo video;
        try {
            String html = new OkHttpUtils(url).sendGet();
            Document doc = Jsoup.parse(html);
            Elements eles = doc.select(".video_list li");
            String videoDetailUrl, sn, title, coverImg;
            for (Element ele : eles) {
                videoDetailUrl = ele.select("a").first().attr("href");
                sn = MD5.encode(webcode + "_" + ele.id());
                title = ele.select("a").first().attr("title");
                coverImg = ele.select("img").first().attr("src");
                System.out.println(videoDetailUrl + "    " + title + "    " + coverImg);



                video = new BaseVideo();


            }
        } catch (Exception e) {

        }
        return mongoTemplate.findAll(BaseVideo.class);
    }

    public static String videoUrlDecode(String vid) {
        String key = "ahP5sd7qQL1Qit5Dg2wsa0OoRag4lk";
        String _0x950cx2 = new String(Base64.getDecoder().decode(vid));
        _0x950cx2 = h(_0x950cx2, key, key);
        _0x950cx2 = j(_0x950cx2);
        _0x950cx2 = c(_0x950cx2);
        int[] code = Arrays.stream(g(_0x950cx2)).mapToInt(Integer::intValue).toArray();
        return fromCharCode(code);
    }

    public static Integer[] g(String _0x950cx2) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < _0x950cx2.length() - 1; i += 2) {
            list.add(Integer.parseInt(_0x950cx2.substring(i, i + 2), 16));
        }
        return list.toArray(new Integer[0]);
    }


    public static String c(String _0x950cx2) {
        String[] _0x950cx2s = _0x950cx2.split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < _0x950cx2s.length; i++) {
            list.add(fromCharCode(Integer.parseInt(_0x950cx2s[i], 2)));
        }
        return String.join("", list.toArray(new String[0]));
    }

    public static String j(String _0x950cx2) {
        String[] _0x950cx2s = _0x950cx2.split(" ");
        for (int i = 0; i < _0x950cx2s.length; i++) {
            _0x950cx2s[i] = a(_0x950cx2s[i], 8);
        }
        return String.join(" ", _0x950cx2s);
    }

    public static String a(String _0x950cx2, int _0x950cx1) {
        return _0x950cx2.length() >= _0x950cx1 ? _0x950cx2 : a(0 + _0x950cx2, _0x950cx1);
    }

    public static String h(String _0x950cx2, String _0x950cx1, String _0x950cx3) {
        String _0x950cx4 = "";
        for (int i = 0; i < _0x950cx2.length(); i++) {
            int k = i % _0x950cx1.length();
            _0x950cx4 += fromCharCode(
                Character.codePointAt(_0x950cx2, i) ^ Character.codePointAt(_0x950cx3, k));
        }
        return _0x950cx4;
    }

    public static String fromCharCode(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }
}
