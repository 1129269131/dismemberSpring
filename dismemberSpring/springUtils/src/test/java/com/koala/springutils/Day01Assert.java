package com.koala.springutils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * Assert的使用
 * Create by koala on 2021-08-28
 */
@SpringBootTest
public class Day01Assert {

    @Test
    void assertTest1() {
        Assert.noNullElements(new Object[]{null}, "Config locations must not be null");
    }

    @Test
    void assertTest2() {
        Assert.notNull("test", "No bean class name for configuration class bean definition");
    }

}
