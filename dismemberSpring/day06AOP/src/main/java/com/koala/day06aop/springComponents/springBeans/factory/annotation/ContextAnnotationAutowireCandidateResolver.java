package com.koala.day06aop.springComponents.springBeans.factory.annotation;

import com.koala.day06aop.springComponents.springBeans.factory.BeanFactory;
import com.koala.day06aop.springComponents.springBeans.factory.config.DependencyDescriptor;
import com.koala.day06aop.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ContextAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {

    @Override
    @Nullable
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        return (isLazy(descriptor) ? buildLazyResolutionProxy(descriptor, beanName) : null);
    }

    protected boolean isLazy(DependencyDescriptor descriptor) {
        for (Annotation ann : descriptor.getAnnotations()) {
            Lazy lazy = AnnotationUtils.getAnnotation(ann, Lazy.class);
            if (lazy != null && lazy.value()) {
                return true;
            }
        }
        MethodParameter methodParam = descriptor.getMethodParameter();
        if (methodParam != null) {
            Method method = methodParam.getMethod();
            if (method == null || void.class == method.getReturnType()) {
                Lazy lazy = AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class);
                if (lazy != null && lazy.value()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final @Nullable String beanName) {
        BeanFactory beanFactory = getBeanFactory();
        Assert.state(beanFactory instanceof DefaultListableBeanFactory,
                "BeanFactory needs to be a DefaultListableBeanFactory");
        final DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

        TargetSource ts = new TargetSource() {
            @Override
            public Class<?> getTargetClass() {
                return descriptor.getDependencyType();
            }
            @Override
            public boolean isStatic() {
                return false;
            }
            @Override
            public Object getTarget() {
                Set<String> autowiredBeanNames = (beanName != null ? new LinkedHashSet<>(1) : null);
                Object target = dlbf.doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
                if (target == null) {
                    Class<?> type = getTargetClass();
                    if (Map.class == type) {
                        return Collections.emptyMap();
                    }
                    else if (List.class == type) {
                        return Collections.emptyList();
                    }
                    else if (Set.class == type || Collection.class == type) {
                        return Collections.emptySet();
                    }
                    throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(),
                            "Optional dependency not present for lazy injection point");
                }
                if (autowiredBeanNames != null) {
                    for (String autowiredBeanName : autowiredBeanNames) {
                        if (dlbf.containsBean(autowiredBeanName)) {
                            dlbf.registerDependentBean(autowiredBeanName, beanName);
                        }
                    }
                }
                return target;
            }
            @Override
            public void releaseTarget(Object target) {
            }
        };

        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        Class<?> dependencyType = descriptor.getDependencyType();
        if (dependencyType.isInterface()) {
            pf.addInterface(dependencyType);
        }
        return pf.getProxy(dlbf.getBeanClassLoader());
    }

}
