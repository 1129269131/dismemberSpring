package com.koala.day06aop;

import com.koala.day06aop.bean.Person;
import com.koala.day06aop.config.MainConfig;
import com.koala.day06aop.springComponents.springContext.context.ApplicationContext;
import com.koala.day06aop.springComponents.springContext.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class Day06AopApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);

        Person person = applicationContext.getBean(Person.class);
        System.out.println(person.getCat());
    }

}
