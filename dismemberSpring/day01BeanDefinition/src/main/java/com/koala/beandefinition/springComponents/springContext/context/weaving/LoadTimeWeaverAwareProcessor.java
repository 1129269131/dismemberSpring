package com.koala.beandefinition.springComponents.springContext.context.weaving;

import com.koala.beandefinition.springComponents.springBeans.factory.BeanFactory;
import com.koala.beandefinition.springComponents.springBeans.factory.BeanFactoryAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-08-29
 */
public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {

    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    @Nullable
    private BeanFactory beanFactory;


    /**
     * Create a new {@code LoadTimeWeaverAwareProcessor} that will
     * auto-retrieve the {@link LoadTimeWeaver} from the containing
     * {@link BeanFactory}, expecting a bean named
     * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
     */
    public LoadTimeWeaverAwareProcessor() {
    }

    /**
     * Create a new {@code LoadTimeWeaverAwareProcessor} for the given
     * {@link LoadTimeWeaver}.
     * <p>If the given {@code loadTimeWeaver} is {@code null}, then a
     * {@code LoadTimeWeaver} will be auto-retrieved from the containing
     * {@link BeanFactory}, expecting a bean named
     * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
     * @param loadTimeWeaver the specific {@code LoadTimeWeaver} that is to be used
     */
    public LoadTimeWeaverAwareProcessor(@Nullable LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    /**
     * Create a new {@code LoadTimeWeaverAwareProcessor}.
     * <p>The {@code LoadTimeWeaver} will be auto-retrieved from
     * the given {@link BeanFactory}, expecting a bean named
     * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
     * @param beanFactory the BeanFactory to retrieve the LoadTimeWeaver from
     */
    public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LoadTimeWeaverAware) {
            LoadTimeWeaver ltw = this.loadTimeWeaver;
            if (ltw == null) {
                Assert.state(this.beanFactory != null,
                        "BeanFactory required if no LoadTimeWeaver explicitly specified");
                ltw = this.beanFactory.getBean(
                        ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME, LoadTimeWeaver.class);
            }
            ((LoadTimeWeaverAware) bean).setLoadTimeWeaver(ltw);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        return bean;
    }

}
