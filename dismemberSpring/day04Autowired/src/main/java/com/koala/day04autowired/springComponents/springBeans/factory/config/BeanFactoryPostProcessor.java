package com.koala.day04autowired.springComponents.springBeans.factory.config;

import com.koala.day04autowired.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;

/**
 * Create by koala on 2021-08-29
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
