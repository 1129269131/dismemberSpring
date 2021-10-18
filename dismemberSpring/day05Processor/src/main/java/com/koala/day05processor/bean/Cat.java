package com.koala.day05processor.bean;

import org.springframework.stereotype.Component;

/**
 * Create by koala on 2021-09-07
 */
@Component
public class Cat {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
