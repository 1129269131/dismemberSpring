package com.koala.day05processor.springComponents.springContext.context.annotation;

import com.koala.day05processor.springComponents.springAop.ScopedProxyUtils;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day05processor.springComponents.springBeans.factory.support.BeanDefinitionRegistry;

/**
 * Create by koala on 2021-09-05
 */
final class ScopedProxyCreator {

    private ScopedProxyCreator() {
    }


    public static BeanDefinitionHolder createScopedProxy(
            BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        return ScopedProxyUtils.createScopedProxy(definitionHolder, registry, proxyTargetClass);
    }

    public static String getTargetBeanName(String originalBeanName) {
        return ScopedProxyUtils.getTargetBeanName(originalBeanName);
    }

}