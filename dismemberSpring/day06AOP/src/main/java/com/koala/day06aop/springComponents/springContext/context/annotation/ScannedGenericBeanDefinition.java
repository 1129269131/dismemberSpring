package com.koala.day06aop.springComponents.springContext.context.annotation;

import com.koala.day06aop.springComponents.springBeans.factory.annotation.AnnotatedBeanDefinition;
import com.koala.day06aop.springComponents.springBeans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-09-05
 */
@SuppressWarnings("serial")
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;


    /**
     * Create a new ScannedGenericBeanDefinition for the class that the
     * given MetadataReader describes.
     * @param metadataReader the MetadataReader for the scanned target class
     */
    public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
        Assert.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());
        setResource(metadataReader.getResource());
    }


    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    @Nullable
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }

}

