package com.koala.day01beandefinition.springComponents.springBeans.factory.support;

import com.koala.day01beandefinition.springComponents.springBeans.factory.config.BeanDefinition;

/**
 * Create by koala on 2021-08-29
 */
public class DefaultBeanNameGenerator implements BeanNameGenerator {

    /**
     * A convenient constant for a default {@code DefaultBeanNameGenerator} instance,
     * as used for {@link AbstractBeanDefinitionReader} setup.
     * @since 5.2
     */
    public static final DefaultBeanNameGenerator INSTANCE = new DefaultBeanNameGenerator();


    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
    }

}