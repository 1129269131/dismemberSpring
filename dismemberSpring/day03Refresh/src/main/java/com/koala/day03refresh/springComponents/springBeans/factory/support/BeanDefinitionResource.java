package com.koala.day03refresh.springComponents.springBeans.factory.support;

import com.koala.day03refresh.springComponents.springBeans.factory.config.BeanDefinition;
import org.springframework.core.io.AbstractResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Create by koala on 2021-08-28
 */
class BeanDefinitionResource extends AbstractResource {

    private final BeanDefinition beanDefinition;


    /**
     * Create a new BeanDefinitionResource.
     * @param beanDefinition the BeanDefinition object to wrap
     */
    public BeanDefinitionResource(BeanDefinition beanDefinition) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        this.beanDefinition = beanDefinition;
    }

    /**
     * Return the wrapped BeanDefinition object.
     */
    public final BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }


    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(
                "Resource cannot be opened because it points to " + getDescription());
    }

    @Override
    public String getDescription() {
        return "BeanDefinition defined in " + this.beanDefinition.getResourceDescription();
    }


    /**
     * This implementation compares the underlying BeanDefinition.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof BeanDefinitionResource &&
                ((BeanDefinitionResource) other).beanDefinition.equals(this.beanDefinition)));
    }

    /**
     * This implementation returns the hash code of the underlying BeanDefinition.
     */
    @Override
    public int hashCode() {
        return this.beanDefinition.hashCode();
    }

}