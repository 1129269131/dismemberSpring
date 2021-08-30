package com.koala.javabase;

import org.junit.jupiter.api.Test;

/**
 * 基本数据类型、包装类、String三者之间的相互转换
 * Create by koala on 2021-08-29
 */
public class Day02wrapper {

    //基本数据类型 ---> 包装类
    @Test
    public void test1(){
        int num1 = 10;
        Integer in1 = new Integer(num1);
    }

    //包装类 ---> 基本数据类型
    @Test
    public void test2(){
        Integer in1 = new Integer(123);
        int i1 = in1.intValue();
    }

    //自动装箱 与 自动拆箱
    @Test
    public void test3(){
        //自动装箱
        int num1 = 123;
        Integer in1 = num1;

        //自动拆箱
        int num2 = in1;
    }

    //基本数据类型、包装类 ---> String类型
    @Test
    public void test4(){
        int num1 = 123;
        String str1 = num1 + "";
        String str2 = String.valueOf(num1);

        Integer i1 = new Integer(123);
        String str3 = String.valueOf(i1);
    }

    //String类型 ---> 基本数据类型、包装类
    @Test
    public void test5(){
        String str1 = "123";
        int num1 = Integer.parseInt(str1);
    }

    @Test
    public void main(){
    }

}
