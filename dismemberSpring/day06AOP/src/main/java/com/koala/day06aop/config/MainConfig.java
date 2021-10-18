package com.koala.day06aop.config;

import com.koala.day06aop.springComponents.springBeans.factory.BeanFactory;
import com.koala.day06aop.springComponents.springBeans.factory.BeanFactoryAware;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Create by koala on 2021-09-05
 */
@ComponentScan("com.koala.day06aop")
@Configuration
public class MainConfig implements BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
