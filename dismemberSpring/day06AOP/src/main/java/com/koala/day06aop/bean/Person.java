package com.koala.day06aop.bean;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Create by koala on 2021-09-05
 */
@Component
public class Person {

    private Cat cat;

    private String name;

    public Person(){
        System.out.println("person创建....");//打断点测试
    }

    @Autowired
    public void setCat(Cat cat) {
        this.cat = cat;//打断点测试
    }

    public Cat getCat() {
        return cat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "cat=" + cat +
                ", name='" + name + '\'' +
                '}';
    }
}
