package com.koala.day05processor.springComponents.springContext.context.annotation;

import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day05processor.springComponents.springCore.core.annotation.AnnotationAttributes;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * Create by koala on 2021-09-05
 */
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {

    private final ScopedProxyMode defaultProxyMode;

    protected Class<? extends Annotation> scopeAnnotationType = Scope.class;


    /**
     * Construct a new {@code AnnotationScopeMetadataResolver}.
     * @see #AnnotationScopeMetadataResolver(ScopedProxyMode)
     * @see ScopedProxyMode#NO
     */
    public AnnotationScopeMetadataResolver() {
        this.defaultProxyMode = ScopedProxyMode.NO;
    }

    /**
     * Construct a new {@code AnnotationScopeMetadataResolver} using the
     * supplied default {@link ScopedProxyMode}.
     * @param defaultProxyMode the default scoped-proxy mode
     */
    public AnnotationScopeMetadataResolver(ScopedProxyMode defaultProxyMode) {
        Assert.notNull(defaultProxyMode, "'defaultProxyMode' must not be null");
        this.defaultProxyMode = defaultProxyMode;
    }


    /**
     * Set the type of annotation that is checked for by this
     * {@code AnnotationScopeMetadataResolver}.
     * @param scopeAnnotationType the target annotation type
     */
    public void setScopeAnnotationType(Class<? extends Annotation> scopeAnnotationType) {
        Assert.notNull(scopeAnnotationType, "'scopeAnnotationType' must not be null");
        this.scopeAnnotationType = scopeAnnotationType;
    }


    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata metadata = new ScopeMetadata();
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
            AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(
                    annDef.getMetadata(), this.scopeAnnotationType);
            if (attributes != null) {
                metadata.setScopeName(attributes.getString("value"));
                ScopedProxyMode proxyMode = attributes.getEnum("proxyMode");
                if (proxyMode == ScopedProxyMode.DEFAULT) {
                    proxyMode = this.defaultProxyMode;
                }
                metadata.setScopedProxyMode(proxyMode);
            }
        }
        return metadata;
    }

}