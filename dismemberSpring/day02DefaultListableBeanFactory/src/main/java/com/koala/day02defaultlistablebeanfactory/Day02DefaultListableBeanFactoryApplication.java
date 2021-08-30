package com.koala.day02defaultlistablebeanfactory;

import com.koala.day02defaultlistablebeanfactory.bean.Person;
import com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.support.ClassPathXmlApplicationContext;

/**
 * 测试：注意看控制台的输出，及输出的位置分析
 * 关键点：
 *      day06：创建档案馆
 *      day07：将BeanDefinition对象保存进档案馆
 *      day08：从档案馆中获取指定beanName的BeanDefinition对象
 * Create by koala on 2021-08-30
 */
public class Day02DefaultListableBeanFactoryApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Person bean = context.getBean(Person.class);
        System.out.println(bean);
    }

}
