package com.koala.beandefinition.springComponents.springContext.context;

import com.koala.beandefinition.springComponents.springBeans.factory.config.BeanFactoryPostProcessor;
import com.koala.beandefinition.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;

import java.io.Closeable;

public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {
    String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

    String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

    String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

    String ENVIRONMENT_BEAN_NAME = "environment";

    String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

    String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";

    String APPLICATION_STARTUP_BEAN_NAME = "applicationStartup";

    String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";

    void setId(String id);

    void setParent(@Nullable ApplicationContext parent);

    void setEnvironment(ConfigurableEnvironment environment);

    @Override
    ConfigurableEnvironment getEnvironment();

    void setApplicationStartup(ApplicationStartup applicationStartup);

    ApplicationStartup getApplicationStartup();

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void addApplicationListener(ApplicationListener<?> listener);

    void setClassLoader(ClassLoader classLoader);

    void addProtocolResolver(ProtocolResolver resolver);

    void refresh() throws BeansException, IllegalStateException;

    void registerShutdownHook();

    @Override
    void close();

    boolean isActive();

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
