package com.koala.day06aop.springComponents.springBeans.factory.annotation;

import com.koala.day06aop.springComponents.springBeans.factory.config.BeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-09-05
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {

    /**
     * Obtain the annotation metadata (as well as basic class metadata)
     * for this bean definition's bean class.
     * @return the annotation metadata object (never {@code null})
     */
    AnnotationMetadata getMetadata();

    /**
     * Obtain metadata for this bean definition's factory method, if any.
     * @return the factory method metadata, or {@code null} if none
     * @since 4.1.1
     */
    @Nullable
    MethodMetadata getFactoryMethodMetadata();

}