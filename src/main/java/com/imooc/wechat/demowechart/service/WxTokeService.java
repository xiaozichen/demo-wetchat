package com.imooc.wechat.demowechart.service;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Maps;
import com.imooc.wechat.demowechart.entity.*;
import com.imooc.wechat.demowechart.util.Util;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WxTokeService {
    private static final String APPKEY = "1fec136dbd19f44743803f89bd55ca62";
    private static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //微信公众号
    private static final String APPID = "wx8ae26b4121499a6e";
    private static final String APPSECRET = "60f1b460f161af89b4582390394f99fd";
    //百度AI
    public static final String APP_ID = "11519092";
    public static final String API_KEY = "q3TlGWWqEBG9uGvlFIBtpvY5";
    public static final String SECRET_KEY = "A14W5VRNG8my1GXYYAyNND0RjzBwxI8A";

    //用于存储token
    private static AccessToken at;

    /**
     * 获取token
     * by 罗召勇 Q群193557337
     */
    private static void getToken() {
        String url = GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
        String tokenStr = Util.get(url);
        JSONObject jsonObject = new JSONObject(tokenStr);
        String token = jsonObject.getStr("access_token");
        String expireIn = jsonObject.getStr("expires_in");
        //创建token对象,并存起来。
        at = new AccessToken(token, expireIn);
    }

    /**
     * 向处暴露的获取token的方法
     *
     * @return by 罗召勇 Q群193557337
     */
    public static String getAccessToken() {
        if (at == null || at.isExpired()) {
            getToken();
        }
        return at.getAccessToken();
    }

    public static boolean check(String token, String timestamp, String nonce, String signature) {
        String[] strs = new String[]{token, timestamp, nonce};
        Arrays.sort(strs);
        String sh1 = sha1(strs[0] + strs[1] + strs[2]);

        return sh1.equalsIgnoreCase(signature);
    }

    private static String sha1(String str) {
        try {
            //获取加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");
            //加密
            byte[] digest = md.digest(str.getBytes());
            //加密处理
            char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(chars[(b >> 4) & 15]);
                sb.append(chars[b & 15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析xml文件
     *
     * @param inputStream
     */
    public static Map<String, String> parseRequestMessage(InputStream inputStream) {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = Maps.newConcurrentMap();
        try {
            Document read = saxReader.read(inputStream);
            //获取根节点
            Element root = read.getRootElement();
            //获取所有子节点
            List<Element> elements = root.elements();
            for (Element e : elements) {
                map.put(e.getName(), e.getStringValue());

            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getRespose(Map<String, String> requestMap) {
        BaseMessage msg = null;
        String msgType = requestMap.get("MsgType");
        switch (msgType) {
            //处理文本消息
            case "text":
                msg = dealTextMessage(requestMap);
                break;
            case "image":
                // msg=dealImage(requestMap);
                break;
            case "voice":

                break;
            case "video":

                break;
            case "shortvideo":

                break;
            case "location":

                break;
            case "link":

                break;
            case "event":
                // msg = dealEvent(requestMap);
                break;
            default:
                break;
        }
        //把消息对象处理为xml数据包
        if (msg != null) {
            return beanToXml(msg);
        }
        return null;
    }

    /**
     * 把消息对象处理为xml数据包
     *
     * @param msg
     * @return by 罗召勇 Q群193557337
     */
    private static String beanToXml(BaseMessage msg) {
        XStream stream = new XStream();
        //设置需要处理XStreamAlias("xml")注释的类
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(ImageMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);
        String xml = stream.toXML(msg);
        return xml;
    }

    /**
     * 处理文本消息
     *
     * @param requestMap
     * @return by 罗召勇 Q群193557337
     */
    private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
        //用户发来的内容
        String msg = requestMap.get("Content");
        if (msg.equals("图文")) {
            List<Article> articles = new ArrayList<>();
            articles.add(new Article("这是图文消息的标题", "这是图文消息的详细介绍", "http://mmbiz.qpic.cn/mmbiz_jpg/dtRJz5K066YczqeHmWFZSPINM5evWoEvW21VZcLzAtkCjGQunCicDubN3v9JCgaibKaK0qGrZp3nXKMYgLQq3M6g/0", "http://www.baidu.com"));
            NewsMessage nm = new NewsMessage(requestMap, articles);
            return nm;
        }
        if (msg.equals("登录")) {
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb6777fffdf5b64a4&redirect_uri=http://www.6sdd.com/weixin/GetUserInfo&response_type=code&scope=snsapi_userinfo#wechat_redirect";
            TextMessage tm = new TextMessage(requestMap, "点击<a href=\"" + url + "\">这里</a>登录");
            return tm;
        }
        //调用方法返回聊天的内容
        String resp = chat(msg);
        if (StringUtils.isEmpty(resp)) {
            resp = msg;
        }
        TextMessage tm = new TextMessage(requestMap, resp);
        return tm;
    }

    /**
     * 调用图灵机器人聊天
     *
     * @param msg 发送的消息
     * @return by 罗召勇 Q群193557337
     */
    private static String chat(String msg) {
        String result = null;
        String url = "http://op.juhe.cn/robot/index";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key", APPKEY);//您申请到的本接口专用的APPKEY
        params.put("info", msg);//要发送给机器人的内容，不要超过30个字符
        params.put("dtype", "");//返回的数据的格式，json或xml，默认为json
        params.put("loc", "");//地点，如北京中关村
        params.put("lon", "");//经度，东经116.234632（小数点后保留6位），需要写为116234632
        params.put("lat", "");//纬度，北纬40.234632（小数点后保留6位），需要写为40234632
        params.put("userid", "");//1~32位，此userid针对您自己的每一个用户，用于上下文的关联
        try {
            result = Util.net(url, params, "GET");
            //解析json
            JSONObject jsonObject = new JSONObject(result);
            //取出error_code
            int code = jsonObject.getInt("error_code");
            if (code != 0) {
                return null;
            }
            //取出返回的消息的内容
            String resp = jsonObject.getJSONObject("result").getStr("text");
            System.err.println(resp);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}