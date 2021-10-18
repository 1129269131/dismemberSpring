package com.koala.day06aop.springComponents.springContext.context.annotation;

import com.koala.day06aop.springComponents.springBeans.factory.config.BeanDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Conventions;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Create by koala on 2021-09-05
 */
public abstract class ConfigurationClassUtils {

    public static final String CONFIGURATION_CLASS_FULL = "full";

    public static final String CONFIGURATION_CLASS_LITE = "lite";

    public static final String CONFIGURATION_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");

    private static final String ORDER_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "order");


    private static final Log logger = LogFactory.getLog(ConfigurationClassUtils.class);

    private static final Set<String> candidateIndicators = new HashSet<>(8);

    static {
        candidateIndicators.add(Component.class.getName());
        candidateIndicators.add(ComponentScan.class.getName());
        candidateIndicators.add(Import.class.getName());
        candidateIndicators.add(ImportResource.class.getName());
    }


    /**
     * Check whether the given bean definition is a candidate for a configuration class
     * (or a nested component class declared within a configuration/component class,
     * to be auto-registered as well), and mark it accordingly.
     * @param beanDef the bean definition to check
     * @param metadataReaderFactory the current factory in use by the caller
     * @return whether the candidate qualifies as (any kind of) configuration class
     */
    public static boolean checkConfigurationClassCandidate(
            BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {

        String className = beanDef.getBeanClassName();
        if (className == null || beanDef.getFactoryMethodName() != null) {
            return false;
        }

        AnnotationMetadata metadata;
        if (beanDef instanceof AnnotatedBeanDefinition &&
                className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
            // Can reuse the pre-parsed metadata from the given BeanDefinition...
            metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
        }
        else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
            // Check already loaded Class if present...
            // since we possibly can't even load the class file for this Class.
            Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass) ||
                    BeanPostProcessor.class.isAssignableFrom(beanClass) ||
                    AopInfrastructureBean.class.isAssignableFrom(beanClass) ||
                    EventListenerFactory.class.isAssignableFrom(beanClass)) {
                return false;
            }
            metadata = AnnotationMetadata.introspect(beanClass);
        }
        else {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                metadata = metadataReader.getAnnotationMetadata();
            }
            catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not find class file for introspecting configuration annotations: " +
                            className, ex);
                }
                return false;
            }
        }

        Map<String, Object> config = metadata.getAnnotationAttributes(Configuration.class.getName());
        if (config != null && !Boolean.FALSE.equals(config.get("proxyBeanMethods"))) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
        }
        else if (config != null || isConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        }
        else {
            return false;
        }

        // It's a full or lite configuration candidate... Let's determine the order value, if any.
        Integer order = getOrder(metadata);
        if (order != null) {
            beanDef.setAttribute(ORDER_ATTRIBUTE, order);
        }

        return true;
    }

    /**
     * Check the given metadata for a configuration class candidate
     * (or nested component class declared within a configuration/component class).
     * @param metadata the metadata of the annotated class
     * @return {@code true} if the given class is to be registered for
     * configuration class processing; {@code false} otherwise
     */
    public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        // Do not consider an interface or an annotation...
        if (metadata.isInterface()) {
            return false;
        }

        // Any of the typical annotations found?
        for (String indicator : candidateIndicators) {
            if (metadata.isAnnotated(indicator)) {
                return true;
            }
        }

        // Finally, let's look for @Bean methods...
        return hasBeanMethods(metadata);
    }

    static boolean hasBeanMethods(AnnotationMetadata metadata) {
        try {
            return metadata.hasAnnotatedMethods(Bean.class.getName());
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
            }
            return false;
        }
    }

    /**
     * Determine the order for the given configuration class metadata.
     * @param metadata the metadata of the annotated class
     * @return the {@code @Order} annotation value on the configuration class,
     * or {@code Ordered.LOWEST_PRECEDENCE} if none declared
     * @since 5.0
     */
    @Nullable
    public static Integer getOrder(AnnotationMetadata metadata) {
        Map<String, Object> orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
        return (orderAttributes != null ? ((Integer) orderAttributes.get(AnnotationUtils.VALUE)) : null);
    }

    /**
     * Determine the order for the given configuration class bean definition,
     * as set by {@link #checkConfigurationClassCandidate}.
     * @param beanDef the bean definition to check
     * @return the {@link Order @Order} annotation value on the configuration class,
     * or {@link Ordered#LOWEST_PRECEDENCE} if none declared
     * @since 4.2
     */
    public static int getOrder(BeanDefinition beanDef) {
        Integer order = (Integer) beanDef.getAttribute(ORDER_ATTRIBUTE);
        return (order != null ? order : Ordered.LOWEST_PRECEDENCE);
    }

}