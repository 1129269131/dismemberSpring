package com.koala.day03refresh;

import com.koala.day03refresh.bean.Person;
import com.koala.day03refresh.springComponents.springContext.context.support.ClassPathXmlApplicationContext;

public class Day03RefreshApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Person bean = context.getBean(Person.class);
        System.out.println(bean);
    }

}
