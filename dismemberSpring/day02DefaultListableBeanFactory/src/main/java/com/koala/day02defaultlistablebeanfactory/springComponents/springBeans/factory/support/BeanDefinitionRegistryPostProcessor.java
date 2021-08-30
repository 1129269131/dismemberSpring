package com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support;

import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.BeansException;

/**
 * Create by koala on 2021-08-29
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}