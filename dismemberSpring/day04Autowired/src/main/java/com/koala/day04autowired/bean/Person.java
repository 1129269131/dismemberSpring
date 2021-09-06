package com.koala.day04autowired.bean;


import org.springframework.stereotype.Component;

/**
 * Create by koala on 2021-09-05
 */
@Component
public class Person {

    public Person(){
        System.out.println("person创建....");//打断点测试
    }


}
