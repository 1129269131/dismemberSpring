package com.koala.day06aop.springComponents.springContext.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-09-05
 */
public class ScopeMetadata {

    private String scopeName = BeanDefinition.SCOPE_SINGLETON;

    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;


    /**
     * Set the name of the scope.
     */
    public void setScopeName(String scopeName) {
        Assert.notNull(scopeName, "'scopeName' must not be null");
        this.scopeName = scopeName;
    }

    /**
     * Get the name of the scope.
     */
    public String getScopeName() {
        return this.scopeName;
    }

    /**
     * Set the proxy-mode to be applied to the scoped instance.
     */
    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        Assert.notNull(scopedProxyMode, "'scopedProxyMode' must not be null");
        this.scopedProxyMode = scopedProxyMode;
    }

    /**
     * Get the proxy-mode to be applied to the scoped instance.
     */
    public ScopedProxyMode getScopedProxyMode() {
        return this.scopedProxyMode;
    }

}