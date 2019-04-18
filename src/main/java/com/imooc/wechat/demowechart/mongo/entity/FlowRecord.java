package com.imooc.wechat.demowechart.mongo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FlowRecord {

    private  String callId;
    private  Integer  type;

    private  Date createTime;

    private  String name ;
}
