package com.koala.day03refresh.springComponents.springBeans.factory.support;

import com.koala.day03refresh.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day03refresh.springComponents.springBeans.factory.support.AbstractBeanDefinitionReader;
import com.koala.day03refresh.springComponents.springBeans.factory.support.BeanDefinitionReaderUtils;

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