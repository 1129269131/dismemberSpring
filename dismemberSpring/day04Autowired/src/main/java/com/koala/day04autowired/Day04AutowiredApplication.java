package com.koala.day04autowired;

import com.koala.day04autowired.bean.Person;
import com.koala.day04autowired.config.MainConfig;
import com.koala.day04autowired.springComponents.springContext.context.ApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.annotation.AnnotationConfigApplicationContext;

public class Day04AutowiredApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);

        Person bean = applicationContext.getBean(Person.class);
    }

}
