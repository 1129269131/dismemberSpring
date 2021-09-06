package com.koala.springutils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StringUtils的使用
 *
 * 参考：https://blog.csdn.net/qq_40542534/article/details/110962293
 * Create by koala on 2021-09-05
 */
@SpringBootTest
public class Day02StringUtils {

    @Test
    void isEmptyTest() {
        System.out.println(StringUtils.isEmpty(""));
    }

    @Test
    void hasLengthTest() {
        System.out.println(StringUtils.hasLength(""));
    }

    @Test
    void hasTextTest() {
        System.out.println(StringUtils.hasText(""));
    }

    @Test
    void containsWhitespaceTest() {
        System.out.println(StringUtils.containsWhitespace(""));
    }

    @Test
    void trimWhitespaceTest() {
        System.out.println(StringUtils.trimWhitespace(" abc "));
    }

    @Test
    void trimAllWhitespaceTest() {
        System.out.println(StringUtils.trimAllWhitespace(" a b c "));
    }

    @Test
    void trimLeadingWhitespaceTest() {
        System.out.println(StringUtils.trimLeadingWhitespace(" a b c "));
    }

    @Test
    void trimTrailingWhitespaceTest() {
        System.out.println(StringUtils.trimTrailingWhitespace(" a b c "));
    }

    @Test
    void trimLeadingCharacterTest() {
        System.out.println(StringUtils.trimLeadingCharacter("tabct",'t'));
    }

    @Test
    void trimTrailingCharacterTest() {
        System.out.println(StringUtils.trimTrailingCharacter("tabct",'t'));
    }

    @Test
    void startsWithIgnoreCaseTest() {
        System.out.println(StringUtils.startsWithIgnoreCase("tEsTabctEsT","test"));
    }

    @Test
    void endsWithIgnoreCaseTest() {
        System.out.println(StringUtils.endsWithIgnoreCase("tEsTabctEsT","test"));
    }

    @Test
    void getFilenameTest() {
        System.out.println(StringUtils.getFilename("爱情与友情.jpg"));
    }

    @Test
    void getFilenameExtensionTest() {
        System.out.println(StringUtils.getFilenameExtension("爱情与友情.jpg"));
    }

    @Test
    void stripFilenameExtensionTest() {
        System.out.println(StringUtils.stripFilenameExtension("爱情与友情.jpg"));
    }

    @Test
    void replaceTest() {
        System.out.println(StringUtils.replace("aabbcc","a","d"));
    }

    @Test
    void deleteTest() {
        System.out.println(StringUtils.delete("aabbcc","aa"));
    }

    @Test
    void deleteAnyTest() {
        System.out.println(StringUtils.deleteAny("aabbcc","aa"));
    }

    @Test
    void toStringArrayTest() {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        String[] result = StringUtils.toStringArray(list);
        Arrays.stream(result).forEach(System.out::println);
    }

}
