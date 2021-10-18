package com.koala.day05processor.springComponents.springBeans.factory.config;

/**
 * Create by koala on 2021-09-05
 */
@FunctionalInterface
public interface BeanDefinitionCustomizer {

    /**
     * Customize the given bean definition.
     */
    void customize(BeanDefinition bd);

}
