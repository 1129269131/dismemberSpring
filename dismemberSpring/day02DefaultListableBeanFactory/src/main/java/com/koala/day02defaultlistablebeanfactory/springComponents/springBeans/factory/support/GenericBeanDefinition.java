package com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support;

import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support.AbstractBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * Create by koala on 2021-08-28
 */
@SuppressWarnings("serial")
public class GenericBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private String parentName;


    /**
     * Create a new GenericBeanDefinition, to be configured through its bean
     * properties and configuration methods.
     * @see #setBeanClass
     * @see #setScope
     * @see #setConstructorArgumentValues
     * @see #setPropertyValues
     */
    public GenericBeanDefinition() {
        super();
    }

    /**
     * Create a new GenericBeanDefinition as deep copy of the given
     * bean definition.
     * @param original the original bean definition to copy from
     */
    public GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }


    @Override
    public void setParentName(@Nullable String parentName) {
        this.parentName = parentName;
    }

    @Override
    @Nullable
    public String getParentName() {
        return this.parentName;
    }


    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenericBeanDefinition)) {
            return false;
        }
        GenericBeanDefinition that = (GenericBeanDefinition) other;
        return (ObjectUtils.nullSafeEquals(this.parentName, that.parentName) && super.equals(other));
    }

    @Override
    public String toString() {
        if (this.parentName != null) {
            return "Generic bean with parent '" + this.parentName + "': " + super.toString();
        }
        return "Generic bean: " + super.toString();
    }

}
