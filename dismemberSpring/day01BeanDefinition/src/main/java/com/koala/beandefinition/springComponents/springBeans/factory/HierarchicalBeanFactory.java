package com.koala.beandefinition.springComponents.springBeans.factory;

import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-29
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    /**
     * Return the parent bean factory, or {@code null} if there is none.
     */
    @Nullable
    BeanFactory getParentBeanFactory();

    /**
     * Return whether the local bean factory contains a bean of the given name,
     * ignoring beans defined in ancestor contexts.
     * <p>This is an alternative to {@code containsBean}, ignoring a bean
     * of the given name from an ancestor bean factory.
     * @param name the name of the bean to query
     * @return whether a bean with the given name is defined in the local factory
     * @see BeanFactory#containsBean
     */
    boolean containsLocalBean(String name);

}
