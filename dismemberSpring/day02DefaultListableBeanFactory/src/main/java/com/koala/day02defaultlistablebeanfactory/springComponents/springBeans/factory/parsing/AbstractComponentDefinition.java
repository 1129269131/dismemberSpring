package com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.parsing;

import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * Create by koala on 2021-08-29
 */
public abstract class AbstractComponentDefinition implements ComponentDefinition {

    /**
     * Delegates to {@link #getName}.
     */
    @Override
    public String getDescription() {
        return getName();
    }

    /**
     * Returns an empty array.
     */
    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[0];
    }

    /**
     * Returns an empty array.
     */
    @Override
    public BeanDefinition[] getInnerBeanDefinitions() {
        return new BeanDefinition[0];
    }

    /**
     * Returns an empty array.
     */
    @Override
    public BeanReference[] getBeanReferences() {
        return new BeanReference[0];
    }

    /**
     * Delegates to {@link #getDescription}.
     */
    @Override
    public String toString() {
        return getDescription();
    }

}
