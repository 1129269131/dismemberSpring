package com.koala.entrance;

import com.koala.entrance.bean.Person;
import com.koala.springComponents.springContext.context.support.ClassPathXmlApplicationContext;

public class EntranceApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Person bean = context.getBean(Person.class);
        System.out.println(bean);
    }

}
