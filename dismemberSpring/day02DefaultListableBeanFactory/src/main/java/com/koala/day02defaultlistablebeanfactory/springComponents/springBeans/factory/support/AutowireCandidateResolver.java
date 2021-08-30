package com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support;

import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.config.DependencyDescriptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericTypeAwareAutowireCandidateResolver;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-29
 */
public interface AutowireCandidateResolver {

    /**
     * Determine whether the given bean definition qualifies as an
     * autowire candidate for the given dependency.
     * <p>The default implementation checks
     * {@link org.springframework.beans.factory.config.BeanDefinition#isAutowireCandidate()}.
     * @param bdHolder the bean definition including bean name and aliases
     * @param descriptor the descriptor for the target method parameter or field
     * @return whether the bean definition qualifies as autowire candidate
     * @see org.springframework.beans.factory.config.BeanDefinition#isAutowireCandidate()
     */
    default boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

    /**
     * Determine whether the given descriptor is effectively required.
     * <p>The default implementation checks {@link DependencyDescriptor#isRequired()}.
     * @param descriptor the descriptor for the target method parameter or field
     * @return whether the descriptor is marked as required or possibly indicating
     * non-required status some other way (e.g. through a parameter annotation)
     * @since 5.0
     * @see DependencyDescriptor#isRequired()
     */
    default boolean isRequired(DependencyDescriptor descriptor) {
        return descriptor.isRequired();
    }

    /**
     * Determine whether the given descriptor declares a qualifier beyond the type
     * (typically - but not necessarily - a specific kind of annotation).
     * <p>The default implementation returns {@code false}.
     * @param descriptor the descriptor for the target method parameter or field
     * @return whether the descriptor declares a qualifier, narrowing the candidate
     * status beyond the type match
     * @since 5.1
     * @see org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#hasQualifier
     */
    default boolean hasQualifier(DependencyDescriptor descriptor) {
        return false;
    }

    /**
     * Determine whether a default value is suggested for the given dependency.
     * <p>The default implementation simply returns {@code null}.
     * @param descriptor the descriptor for the target method parameter or field
     * @return the value suggested (typically an expression String),
     * or {@code null} if none found
     * @since 3.0
     */
    @Nullable
    default Object getSuggestedValue(DependencyDescriptor descriptor) {
        return null;
    }

    /**
     * Build a proxy for lazy resolution of the actual dependency target,
     * if demanded by the injection point.
     * <p>The default implementation simply returns {@code null}.
     * @param descriptor the descriptor for the target method parameter or field
     * @param beanName the name of the bean that contains the injection point
     * @return the lazy resolution proxy for the actual dependency target,
     * or {@code null} if straight resolution is to be performed
     * @since 4.0
     */
    @Nullable
    default Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        return null;
    }

    /**
     * Return a clone of this resolver instance if necessary, retaining its local
     * configuration and allowing for the cloned instance to get associated with
     * a new bean factory, or this original instance if there is no such state.
     * <p>The default implementation creates a separate instance via the default
     * class constructor, assuming no specific configuration state to copy.
     * Subclasses may override this with custom configuration state handling
     * or with standard {@link Cloneable} support (as implemented by Spring's
     * own configurable {@code AutowireCandidateResolver} variants), or simply
     * return {@code this} (as in {@link SimpleAutowireCandidateResolver}).
     * @since 5.2.7
     * @see GenericTypeAwareAutowireCandidateResolver#cloneIfNecessary()
     * @see DefaultListableBeanFactory#copyConfigurationFrom
     */
    default AutowireCandidateResolver cloneIfNecessary() {
        return BeanUtils.instantiateClass(getClass());
    }

}
