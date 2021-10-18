package com.koala.day06aop.springComponents.springContext.context.annotation;

import com.koala.day06aop.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day06aop.springComponents.springBeans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Create by koala on 2021-09-05
 */
public interface ImportBeanDefinitionRegistrar {

    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
     * registered here, due to lifecycle constraints related to {@code @Configuration}
     * class processing.
     * <p>The default implementation delegates to
     * {@link #registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)}.
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry current bean definition registry
     * @param importBeanNameGenerator the bean name generator strategy for imported beans:
     * {@link ConfigurationClassPostProcessor#IMPORT_BEAN_NAME_GENERATOR} by default, or a
     * user-provided one if {@link ConfigurationClassPostProcessor#setBeanNameGenerator}
     * has been set. In the latter case, the passed-in strategy will be the same used for
     * component scanning in the containing application context (otherwise, the default
     * component-scan naming strategy is {@link AnnotationBeanNameGenerator#INSTANCE}).
     * @since 5.2
     * @see ConfigurationClassPostProcessor#IMPORT_BEAN_NAME_GENERATOR
     * @see ConfigurationClassPostProcessor#setBeanNameGenerator
     */
    default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
                                         BeanNameGenerator importBeanNameGenerator) {

        registerBeanDefinitions(importingClassMetadata, registry);
    }

    /**
     * Register bean definitions as necessary based on the given annotation metadata of
     * the importing {@code @Configuration} class.
     * <p>Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
     * registered here, due to lifecycle constraints related to {@code @Configuration}
     * class processing.
     * <p>The default implementation is empty.
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry current bean definition registry
     */
    default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }

}
