package com.koala.day03refresh.springComponents.springBeans.factory.support;

import com.koala.day03refresh.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day03refresh.springComponents.springBeans.factory.config.DependencyDescriptor;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-29
 */
public class SimpleAutowireCandidateResolver implements AutowireCandidateResolver {

    /**
     * Shared instance of {@code SimpleAutowireCandidateResolver}.
     * @since 5.2.7
     */
    public static final SimpleAutowireCandidateResolver INSTANCE = new SimpleAutowireCandidateResolver();


    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

    @Override
    public boolean isRequired(DependencyDescriptor descriptor) {
        return descriptor.isRequired();
    }

    @Override
    public boolean hasQualifier(DependencyDescriptor descriptor) {
        return false;
    }

    @Override
    @Nullable
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        return null;
    }

    @Override
    @Nullable
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        return null;
    }

    /**
     * This implementation returns {@code this} as-is.
     * @see #INSTANCE
     */
    @Override
    public AutowireCandidateResolver cloneIfNecessary() {
        return this;
    }

}
