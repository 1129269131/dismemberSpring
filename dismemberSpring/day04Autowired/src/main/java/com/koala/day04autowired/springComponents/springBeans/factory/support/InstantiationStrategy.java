package com.koala.day04autowired.springComponents.springBeans.factory.support;

import com.koala.day04autowired.springComponents.springBeans.factory.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface InstantiationStrategy {

    /**
     * Return an instance of the bean with the given name in this factory.
     * @param bd the bean definition
     * @param beanName the name of the bean when it is created in this context.
     * The name can be {@code null} if we are autowiring a bean which doesn't
     * belong to the factory.
     * @param owner the owning BeanFactory
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation attempt failed
     */
    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner)
            throws BeansException;

    /**
     * Return an instance of the bean with the given name in this factory,
     * creating it via the given constructor.
     * @param bd the bean definition
     * @param beanName the name of the bean when it is created in this context.
     * The name can be {@code null} if we are autowiring a bean which doesn't
     * belong to the factory.
     * @param owner the owning BeanFactory
     * @param ctor the constructor to use
     * @param args the constructor arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation attempt failed
     */
    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                       Constructor<?> ctor, Object... args) throws BeansException;

    /**
     * Return an instance of the bean with the given name in this factory,
     * creating it via the given factory method.
     * @param bd the bean definition
     * @param beanName the name of the bean when it is created in this context.
     * The name can be {@code null} if we are autowiring a bean which doesn't
     * belong to the factory.
     * @param owner the owning BeanFactory
     * @param factoryBean the factory bean instance to call the factory method on,
     * or {@code null} in case of a static factory method
     * @param factoryMethod the factory method to use
     * @param args the factory method arguments to apply
     * @return a bean instance for this bean definition
     * @throws BeansException if the instantiation attempt failed
     */
    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                       @Nullable Object factoryBean, Method factoryMethod, Object... args)
            throws BeansException;

}
