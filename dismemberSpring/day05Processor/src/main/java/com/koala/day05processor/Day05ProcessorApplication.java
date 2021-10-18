package com.koala.day05processor;

import com.koala.day05processor.bean.Person;
import com.koala.day05processor.config.MainConfig;
import com.koala.day05processor.springComponents.springContext.context.ApplicationContext;
import com.koala.day05processor.springComponents.springContext.context.annotation.AnnotationConfigApplicationContext;

public class Day05ProcessorApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);

        Person person = applicationContext.getBean(Person.class);
        System.out.println(person.getCat());
    }

}
