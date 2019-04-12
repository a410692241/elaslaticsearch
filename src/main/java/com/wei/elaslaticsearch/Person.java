package com.wei.elaslaticsearch;

import com.sun.scenario.effect.impl.prism.PrImage;

public class Person {
    private String id;
    private Integer age;
    private boolean isStudent;
    private String sex;
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
