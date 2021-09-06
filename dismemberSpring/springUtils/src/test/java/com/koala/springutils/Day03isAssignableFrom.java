package com.koala.springutils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * isAssignableFrom的使用：
 *   isAssignableFrom()方法与instanceof关键字的区别：
 *      1、isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断。
 *      2、isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类。
 *   使用方法：
 *      父类.class.isAssignableFrom(子类.class)
 *      子类实例 instanceof 父类类型
 * Create by koala on 2021-09-06
 */
@SpringBootTest
public class Day03isAssignableFrom {

    @Test
    void isAssignableFromTest() {
        System.out.println(Object.class.isAssignableFrom(String.class));
    }

    @Test
    void instanceofTest() {
        String str = new String("abc");
        System.out.println(str instanceof Object);
    }

}
