package com.koala.day06aop.springComponents.springAop;

import com.koala.day06aop.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day06aop.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day06aop.springComponents.springBeans.factory.support.AbstractBeanDefinition;
import com.koala.day06aop.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day06aop.springComponents.springBeans.factory.support.RootBeanDefinition;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-09-05
 */
public abstract class ScopedProxyUtils {

    private static final String TARGET_NAME_PREFIX = "scopedTarget.";

    private static final int TARGET_NAME_PREFIX_LENGTH = TARGET_NAME_PREFIX.length();


    /**
     * Generate a scoped proxy for the supplied target bean, registering the target
     * bean with an internal name and setting 'targetBeanName' on the scoped proxy.
     * @param definition the original bean definition
     * @param registry the bean definition registry
     * @param proxyTargetClass whether to create a target class proxy
     * @return the scoped proxy definition
     * @see #getTargetBeanName(String)
     * @see #getOriginalBeanName(String)
     */
    public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
                                                         BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        String originalBeanName = definition.getBeanName();
        BeanDefinition targetDefinition = definition.getBeanDefinition();
        String targetBeanName = getTargetBeanName(originalBeanName);

        // Create a scoped proxy definition for the original bean name,
        // "hiding" the target bean in an internal target definition.
        RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
        proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
        proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
        proxyDefinition.setSource(definition.getSource());
        proxyDefinition.setRole(targetDefinition.getRole());

        proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
        if (proxyTargetClass) {
            targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            // ScopedProxyFactoryBean's "proxyTargetClass" default is TRUE, so we don't need to set it explicitly here.
        }
        else {
            proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
        }

        // Copy autowire settings from original bean definition.
        proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
        proxyDefinition.setPrimary(targetDefinition.isPrimary());
        if (targetDefinition instanceof AbstractBeanDefinition) {
            proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
        }

        // The target bean should be ignored in favor of the scoped proxy.
        targetDefinition.setAutowireCandidate(false);
        targetDefinition.setPrimary(false);

        // Register the target bean as separate bean in the factory.
        registry.registerBeanDefinition(targetBeanName, targetDefinition);

        // Return the scoped proxy definition as primary bean definition
        // (potentially an inner bean).
        return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
    }

    /**
     * Generate the bean name that is used within the scoped proxy to reference the target bean.
     * @param originalBeanName the original name of bean
     * @return the generated bean to be used to reference the target bean
     * @see #getOriginalBeanName(String)
     */
    public static String getTargetBeanName(String originalBeanName) {
        return TARGET_NAME_PREFIX + originalBeanName;
    }

    /**
     * Get the original bean name for the provided {@linkplain #getTargetBeanName
     * target bean name}.
     * @param targetBeanName the target bean name for the scoped proxy
     * @return the original bean name
     * @throws IllegalArgumentException if the supplied bean name does not refer
     * to the target of a scoped proxy
     * @since 5.1.10
     * @see #getTargetBeanName(String)
     * @see #isScopedTarget(String)
     */
    public static String getOriginalBeanName(@Nullable String targetBeanName) {
        Assert.isTrue(isScopedTarget(targetBeanName), () -> "bean name '" +
                targetBeanName + "' does not refer to the target of a scoped proxy");
        return targetBeanName.substring(TARGET_NAME_PREFIX_LENGTH);
    }

    /**
     * Determine if the {@code beanName} is the name of a bean that references
     * the target bean within a scoped proxy.
     * @since 4.1.4
     */
    public static boolean isScopedTarget(@Nullable String beanName) {
        return (beanName != null && beanName.startsWith(TARGET_NAME_PREFIX));
    }

}
