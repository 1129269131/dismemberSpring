package com.koala.springutils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;

/**
 * ClassUtils的使用
 *
 * 参考：
 *      https://blog.csdn.net/qq_28269891/article/details/93742002?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522163093929816780262563966%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=163093929816780262563966&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~rank_v29_ecpm-6-93742002.first_rank_v2_pc_rank_v29&utm_term=ClassUtils&spm=1018.2226.3001.4187
 * Create by koala on 2021-09-06
 */
@SpringBootTest
public class Day05ClassUtils {

    @Test
    void getDefaultClassLoaderTest() {
        System.out.println(ClassUtils.getDefaultClassLoader());
    }

    @Test
    public void forNameTest() throws Exception {
        System.out.println(ClassUtils.forName("int", ClassUtils.getDefaultClassLoader()));
    }

    @Test
    public void getShortNameTest() {
        System.out.println(ClassUtils.getShortName(getClass()));
    }

    @Test
    public void getShortNameAsPropertyTest() {
        System.out.println(ClassUtils.getShortNameAsProperty(getClass()));
    }

    @Test
    public void getClassFileNameTest() {
        System.out.println(ClassUtils.getClassFileName(getClass()));
    }

    @Test
    public void getPackageNameTest() {
        System.out.println(ClassUtils.getPackageName(getClass()));
    }

    @Test
    public void getQualifiedNameTest() {
        System.out.println(ClassUtils.getQualifiedName(String[].class));
    }

}
