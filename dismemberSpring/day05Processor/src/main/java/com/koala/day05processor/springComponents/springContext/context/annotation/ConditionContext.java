package com.koala.day05processor.springComponents.springContext.context.annotation;

import com.koala.day05processor.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-09-05
 */
public interface ConditionContext {

    /**
     * Return the {@link BeanDefinitionRegistry} that will hold the bean definition
     * should the condition match.
     * @throws IllegalStateException if no registry is available (which is unusual:
     * only the case with a plain {@link ClassPathScanningCandidateComponentProvider})
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * Return the {@link ConfigurableListableBeanFactory} that will hold the bean
     * definition should the condition match, or {@code null} if the bean factory is
     * not available (or not downcastable to {@code ConfigurableListableBeanFactory}).
     */
    @Nullable
    ConfigurableListableBeanFactory getBeanFactory();

    /**
     * Return the {@link Environment} for which the current application is running.
     */
    Environment getEnvironment();

    /**
     * Return the {@link ResourceLoader} currently being used.
     */
    ResourceLoader getResourceLoader();

    /**
     * Return the {@link ClassLoader} that should be used to load additional classes
     * (only {@code null} if even the system ClassLoader isn't accessible).
     * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
     */
    @Nullable
    ClassLoader getClassLoader();

}
