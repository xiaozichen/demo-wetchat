package com.imooc.wechat.demowechart;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.imooc.wechat.demowechart.mongo.entity.FlowRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FlowRecordTest extends DemoBaseTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public  void test02(){
        Criteria criteria= new Criteria();
        criteria.and("createTime").gte(DateUtil.parse("2019-04-12 13:00:00"));
        List<FlowRecord> flowRecords = mongoTemplate.find(Query.query(criteria), FlowRecord.class, "flowRecord");
        for (FlowRecord json : flowRecords){
            System.err.println(json);
        }

    }
    @Test
    public  void test01() throws ParseException {
        //LocalDateTime localDateTime = new LocalDateTime();
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String starttime="2019-04-12 23:20:23";
        String endtime="2016-07-09";
        Criteria criteria= new Criteria();
        criteria.and("createTime").gte(format.parse(starttime));
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
               // Aggregation.match(Criteria.where("createTime").gte(cal.getTime())),
                Aggregation.project("createTime","type","count"),
                Aggregation.group("type").count().as("count").last("createTime").as("createTime")

        );
        System.err.println(aggregation.toString());
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(aggregation, "flowRecord", JSONObject.class);

        List<JSONObject> mappedResults = results.getMappedResults();
       for (JSONObject json : mappedResults){
           System.err.println(json);
       }

    }
}
