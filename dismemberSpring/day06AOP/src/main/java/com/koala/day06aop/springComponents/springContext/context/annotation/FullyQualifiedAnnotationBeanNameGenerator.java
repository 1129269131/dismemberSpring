package com.koala.day06aop.springComponents.springContext.context.annotation;

import com.koala.day06aop.springComponents.springBeans.factory.config.BeanDefinition;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-09-05
 */
public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * A convenient constant for a default {@code FullyQualifiedAnnotationBeanNameGenerator}
     * instance, as used for configuration-level import purposes.
     * @since 5.2.11
     */
    public static final FullyQualifiedAnnotationBeanNameGenerator INSTANCE =
            new FullyQualifiedAnnotationBeanNameGenerator();


    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        return beanClassName;
    }

}