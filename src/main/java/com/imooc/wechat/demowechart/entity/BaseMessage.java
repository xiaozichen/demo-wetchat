package com.imooc.wechat.demowechart.entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
public class BaseMessage {

	@XStreamAlias("ToUserName")
	private String toUserName;
	@XStreamAlias("FromUserName")
	private String fromUserName;
	@XStreamAlias("CreateTime")
	private String createTime;
	@XStreamAlias("MsgType")
	private String msgType;

	public BaseMessage(Map<String, String> requestMap) {
		this.toUserName=requestMap.get("FromUserName");
		this.fromUserName=requestMap.get("ToUserName");
		this.createTime=System.currentTimeMillis()/1000+"";
	}


}
