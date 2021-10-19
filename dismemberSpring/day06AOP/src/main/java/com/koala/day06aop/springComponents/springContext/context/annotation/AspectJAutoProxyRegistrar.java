package com.koala.day06aop.springComponents.springContext.context.annotation;

import com.koala.day06aop.springComponents.springAop.AopConfigUtils;
import com.koala.day06aop.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day06aop.springComponents.springCore.core.annotation.AnnotationAttributes;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Create by koala on 2021-10-19
 */
class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * Register, escalate, and configure the AspectJ auto proxy creator based on the value
     * of the @{@link org.springframework.context.annotation.EnableAspectJAutoProxy#proxyTargetClass()} attribute on the importing
     * {@code @Configuration} class.
     */
    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //day24：注册切面的基于注解的自动代理创建器
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

        AnnotationAttributes enableAspectJAutoProxy =
                AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
        if (enableAspectJAutoProxy != null) {
            if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
                AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
            }
            if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
                AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
            }
        }
    }

}
