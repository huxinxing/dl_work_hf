package com.bcb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Base64;

/*
* 映射地址
* */
@Slf4j
@RequestMapping("/admin/url")
@Controller
public class UrlController {
    /*
     *点击url返回url
     * */
    //@LoginRequired
    @RequestMapping(value = "/getUrl",method = RequestMethod.GET)
    public String getUrl(String url) throws  Exception{
        try{
            String   mytext2   =   java.net.URLDecoder.decode(url,   "utf-8");
            //String str = new String(new BASE64Decoder().decodeBuffer(mytext2));
            String str = new String(Base64.getDecoder().decode(mytext2), "utf-8");
            return str;
        }catch (Exception e){
            log.error("映射地址失败",e);
        }
        return "";

    }
}
