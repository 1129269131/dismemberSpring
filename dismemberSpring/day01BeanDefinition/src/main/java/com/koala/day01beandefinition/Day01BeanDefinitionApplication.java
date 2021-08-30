package com.koala.day01beandefinition;

import com.koala.day01beandefinition.bean.Person;
import com.koala.day01beandefinition.springComponents.springContext.context.support.ClassPathXmlApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试：注意看控制台的输出，及输出的位置分析
 * 关键点：
 *      day01：准备读取xml内容的读取器
 *      day02：利用Dom解析把xml解析成Document
 *      day03：创建BeanDefinitionDocumentReader对象，并读取Document里面的对象
 *      dat04：找到BeanDefinitionParserDelegate
 *      day05：解析成BeanDefinition对象
 * Create by koala on 2021-08-29
 */
public class Day01BeanDefinitionApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Person bean = context.getBean(Person.class);
        System.out.println(bean);
    }

}
