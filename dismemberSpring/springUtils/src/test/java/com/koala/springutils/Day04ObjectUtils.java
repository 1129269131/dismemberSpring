package com.koala.springutils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

/**
 * ObjectUtils的使用
 * Create by koala on 2021-09-06
 */
@SpringBootTest
public class Day04ObjectUtils {

    @Test
    void isEmptyTest() {
        System.out.println(ObjectUtils.isEmpty(null));
    }

    @Test
    void isNotEmptyTest() {
        System.out.println(ObjectUtils.isArray(null));
    }

    @Test
    void addObjectToArrayTest() {
        Object[] objs = ObjectUtils.addObjectToArray(new Object[]{}, new StringBuffer("abc"));
        Arrays.stream(objs).forEach(System.out::println);
    }

    @Test
    void toObjectArrayTest() {
        Object[] objs = ObjectUtils.toObjectArray(new String[]{"a","b","c"});
        Arrays.stream(objs).forEach(System.out::println);
    }

}
