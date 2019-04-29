package com.wei.elaslaticsearch.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
/*自动建立的索引名和类型*/
@Document(indexName = "index_person", type = "person")
public class Person {
    /*标志主键*/
    @Id
    private String id;
    @Field
    private Integer age;
    /*建立索引忽略该字段*/
    @Field(ignoreFields={"isStudent"})
    private boolean isStudent;
    @Field
    private String sex;
    /*指定分词器*/
    @Field(analyzer = "ik_smart")
    private String name;


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsStudent(boolean student) {
            isStudent = student;
    }

    public boolean getIsStudent() {
        return isStudent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
