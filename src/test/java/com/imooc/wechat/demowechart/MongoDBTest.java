package com.imooc.wechat.demowechart;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.imooc.wechat.demowechart.mongo.dao.MongoTestDao;
import com.imooc.wechat.demowechart.mongo.entity.FlowRecord;
import com.imooc.wechat.demowechart.mongo.entity.MongoTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.assertj.core.util.Lists;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.print.Doc;
import java.util.*;


public class MongoDBTest extends DemoBaseTests {

    @Autowired
    private MongoTestDao mtdao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public void saveTest() throws Exception {
        MongoTest mgtest=new MongoTest();
        mgtest.setAge(33);
        mgtest.setId(11);
        mgtest.setName("ceshi");
        mtdao.saveTest(mgtest);
    }

    @Test
    public void queryOne(){
       // Query query= Criteria.query(Criteria.where("area").is("china").and("age").gte(17));
        Query query= new Query(Criteria.where("name").is("ceshi").and("age").gte(17));
        MongoTest one = mongoTemplate.findOne(query, MongoTest.class);
        System.err.println(one);
    }
    @Test
    public void queryField(){
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", "ceshi");  //查询条件

        BasicDBObject fieldsObject=new BasicDBObject();
//指定返回的字段
        fieldsObject.put("name", true);
        fieldsObject.put("age", true);

        //Query query = new BasicQuery(JSONObject);

    }

    @Test
    public void saveFlowRecord(){
        for(int i =0;i<10 ;i++){
            FlowRecord record= new FlowRecord();
            record.setCallId(UUID.randomUUID().toString());
            record.setCreateTime(new Date());
            record.setName("name"+i);
            if(i % 2 == 0){
                record.setType(1);
            }else {
                record.setType(2);
            }
            mongoTemplate.save(record);
        }


    }

    @Test
    public void testCount(){
        Aggregation agg = Aggregation.newAggregation(

                // 第一步：挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("callId", "name", "callIdNum")
                        .andExpression("{$dateToString:{format:'%Y-%m-%d',date: {$add:{'$createTime',8*60*60000}}}}").as("createTime")
                        .andExpression("{$cond:{if:{$eq:['$type',1]},then:1,else:0}}").as("type")
                ,

                // 第三步：分组条件，设置分组字段
                Aggregation.group("callId").count().as("callIdNum").last("name").as("name").last("type").as("type")
                .last("createTime").as("createTime")

                ,

                Aggregation.project("callId", "name", "callIdNum","createTime","type")

        );

        AggregationResults<JSONObject> results = mongoTemplate.aggregate(agg, "flowRecord", JSONObject.class);
        List<JSONObject> mappedResults = results.getMappedResults();
        System.err.println(mappedResults);

    }




     /****db.inventory.find(
     *   {"item":"abc1"}
     * )
     */
    @Test
    public void testAggregation(){
        MongoCollection<Document> collection = mongoTemplate.getCollection("inventory");
        Document document = new Document();
        document.put("item","abc1");
        FindIterable<Document> documents = collection.find(document);
        MongoCursor<Document> iterator = documents.iterator();
        while (iterator.hasNext()){
            Document next = iterator.next();
            System.err.println(next);
        }
    }
    /***
     *
     * db.inventory.aggregate(
     *
     *       {
     *          $project:
     *            {
     *              item: 1,
     *              discount:
     *                {
     *                  $cond: { if: { $gte: [ "$qty", 250 ] }, then: 30, else: 20 }
     *                }
     *            }
     *       }
     *
     * )
     **/
    @Test
    public void testAggregationIf(){
        MongoCollection<Document> collection = mongoTemplate.getCollection("inventory");
        //Document.parse("{$cond: {if:{ $gte: [ '$qty', 250 ] },  then: 30, else: 20 }}");
        //Document docIf = new Document("if",new Document("$gte","['$qty', 250 ]")).append("then",30).append("else",20);
         Document docIf = new Document("else",20);
         //.append("then",30).append("if",new Document("$gte","['$qty', 250 ]"));
        docIf.put("if",new Document("$gte","['$qty', 250 ]"));
        docIf.put("then",30);
        Document doc = new Document(" $cond",docIf);
        Document docProject = new Document("discount", doc);
        docProject.put("item",1);
        Document document  =   new Document("$project",docProject);
        JSON json = new JSONObject(document);
        System.err.println(json);
        AggregateIterable<Document> resultset = collection.aggregate(Lists.newArrayList(document));
        MongoCursor<Document> iterator = resultset.iterator();
        while (iterator.hasNext()){
            Document next = iterator.next();
            System.err.println(next);
        }

    }
    /***
     *
     * db.inventory.aggregate(
     *
     *       {
     *          $project:
     *            {
     *              item: 1,
     *              discount:
     *                {
     *                  $cond: { if: { $gte: [ "$qty", 250 ] }, then: 30, else: 20 }
     *                }
     *            }
     *       }
     *
     * )
     **/
    @Test
    public void test01(){
        MongoCollection<Document> collection = mongoTemplate.getCollection("inventory");
        Document docCond = Document.parse("{$cond: { if: { $gte: [ \"$qty\", 250 ] }, then: 30, else: 20 }}");
        Document document = new Document("item",1).append("discount",docCond);
        Document docProject = new Document("$project",document);
        JSON json = new JSONObject(docProject);
        System.err.println(json);
        AggregateIterable<Document> resultset = collection.aggregate(Lists.newArrayList(docProject));
        MongoCursor<Document> iterator = resultset.iterator();
        while (iterator.hasNext()){
            Document next = iterator.next();
            System.err.println(next);
        }
       // collection.updateOne()
        //collection.r

    }
    @Test
    public void test02(){
      Document document = Document.parse(" {" +
              "         $project:" +
              "           { " +
              "item: 1, " +
              "discount: " +
              "{ " +
              "$cond: {if:{ $gte: [ '$qty', 250 ] },  then: 30, else: 20 }\n" +
              "}" +
              "}" +
              "}") ;
    }


    /**
     *  * db.getCollection('parking_record').aggregate(
     *      *         {$match : {"appId" : "2e1800b22ae70600", "leaveTime" : {"$gt" : ISODate("2017-07-12T00:00:00"), "$lt" : ISODate("2017-07-13T00:00:00")}}},
     *      *         {$group : {"_id" : "$leaveMethod", "count" : {$sum : 1}}},
     *      *         {$sort : {"_id" : 1}}
     *      *     )
     * 根据日期统计离场方式
     * @param app_id 插件ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return {"ManualLeave":2,"AutoLeave":4}
     * @throws Exception
     */
    public String aggregateLeaveMethodByDate(String app_id, Date beginDate, Date endDate) throws Exception {

        MongoCollection<Document> collection = mongoTemplate.getCollection("");
        Document sub_match = new Document();
        sub_match.put("appId", app_id);
        sub_match.put("leaveTime", new Document("$gt", beginDate).append("$lt", endDate));

        Document sub_group = new Document();
        sub_group.put("_id", "$leaveMethod");
        sub_group.put("count", new Document("$sum", 1));

        Document match = new Document("$match", sub_match);
        Document group = new Document("$group", sub_group);
        Document sort = new Document("$sort", new Document("_id", 1));

        List<Document> aggregateList = new ArrayList<Document>();
        aggregateList.add(match);
        aggregateList.add(group);
        aggregateList.add(sort);

        JSONObject ret_obj = new JSONObject();
        AggregateIterable<Document> resultset = collection.aggregate(aggregateList);
        MongoCursor<Document> cursor = resultset.iterator();

        try {
            while(cursor.hasNext()) {
                Document item_doc = cursor.next();
                int leaveMethod = item_doc.getInteger("_id", 0);
                int count = item_doc.getInteger("count", 0);

               // LeaveMethodEnum leaveMethodVal = LeaveMethodEnum.fromType(leaveMethod);
                //ret_obj.put(leaveMethodVal.name(), count);
            }
        } finally {
            cursor.close();
        }

        return ret_obj.toString();
    }

}
