package com.koala.javabase;

import org.junit.jupiter.api.Test;

/**
 * Create by koala on 2021-08-29
 */
public class Day03stringMethod {

    @Test
    public void test1() {
        String s1 = "HelloWorld";

        System.out.println(s1.length());
        System.out.println(s1.charAt(0));
        System.out.println(s1.isEmpty());
        System.out.println(s1.isBlank());

        String s2 = s1.toLowerCase();
        System.out.println(s1);
        System.out.println(s2);

        String s3 = "   he  llo   world   ";
        String s4 = s3.trim();
        System.out.println(s4);
    }

    @Test
    public void test2() {
        String s1 = "HelloWorld";
        String s2 = "helloworld";
        System.out.println(s1.equals(s2));
        System.out.println(s1.equalsIgnoreCase(s2));

        String s3 = "abc";
        String s4 = s3.concat("def");
        System.out.println(s4);

        String s5 = "abc";
        String s6 = new String ("ABC");
        System.out.println(s5.compareTo(s6));
        System.out.println(s5.compareToIgnoreCase(s6));

        String s7 = "北京尚硅谷教育";
        String s8 = s7.substring(2);
        System.out.println(s8);
        String s9 = s7.substring(2, 5);
        System.out.println(s9);
    }

    @Test
    public void main(){
    }

}
