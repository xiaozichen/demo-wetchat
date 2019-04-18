package com.imooc.wechat.demowechart.controller;

import com.imooc.wechat.demowechart.service.WxTokeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
public class WxTokenController {

    @GetMapping("/wxToken")
    public String  getAccentToken(HttpServletRequest request){
        /*signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        timestamp	时间戳
        nonce	随机数
        echostr	随机字符串*/
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        String  token = "cs";
        if(WxTokeService.check( token, timestamp, nonce,signature)){
            System.err.println("接入成功");
            return echostr;
        }else {
            System.err.println("接入失败");
        }
        return  null ;

    }
    @PostMapping("/wxToken")
    public String   getPostAccentToken(HttpServletRequest request){
        try {
            Map<String, String> requestMap = WxTokeService.parseRequestMessage(request.getInputStream());
            System.err.println(requestMap);

            //处理消息和事件推送
            System.out.println(requestMap);
            //准备回复的数据包
            String respXml = WxTokeService.getRespose(requestMap);
            System.err.println(respXml);
            return respXml;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null ;



    }


}
