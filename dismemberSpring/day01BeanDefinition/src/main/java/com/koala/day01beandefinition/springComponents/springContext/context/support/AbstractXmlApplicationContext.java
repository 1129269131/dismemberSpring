package com.koala.day01beandefinition.springComponents.springContext.context.support;

import com.koala.day01beandefinition.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.XmlBeanDefinitionReader;
import com.koala.day01beandefinition.springComponents.springContext.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * Create by koala on 2021-08-26
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

    private boolean validating = true;

    public AbstractXmlApplicationContext() {
    }

    public AbstractXmlApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory. day07：准备读取xml内容的读取器
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        System.out.println("day01：准备读取xml内容的读取器");

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this); //day07：持有ioc容器的环境类
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
        reader.setValidating(this.validating);
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        Resource[] configResources = getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        String[] configLocations = getConfigLocations(); //day07：可以一次传入很多配置文件
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations); //day07：读取文件
        }
    }

    @Nullable
    protected Resource[] getConfigResources() {
        return null;
    }

}
