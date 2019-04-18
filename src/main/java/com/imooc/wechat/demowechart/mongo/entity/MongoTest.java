package com.imooc.wechat.demowechart.mongo.entity;

import lombok.Data;

@Data
public class MongoTest {
    private Integer id;
    private Integer age;
    private String name;


    @Override
    public String toString() {
        return "MongoTest{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
