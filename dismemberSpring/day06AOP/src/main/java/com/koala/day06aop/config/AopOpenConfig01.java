package com.koala.day06aop.config;

import com.koala.day06aop.springComponents.springBeans.factory.BeanFactory;
import com.koala.day06aop.springComponents.springBeans.factory.BeanFactoryAware;
import com.koala.day06aop.springComponents.springContext.context.annotation.EnableAspectJAutoProxy;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Configuration;

/**
 * Create by koala on 2021-09-12
 */
@EnableAspectJAutoProxy //开启自动代理
@Configuration
public class AopOpenConfig01 implements BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
