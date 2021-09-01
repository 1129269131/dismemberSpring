package com.koala.day03refresh.springComponents.springContext.context.support;

import com.koala.day03refresh.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day03refresh.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day03refresh.springComponents.springContext.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * Create by koala on 2021-08-26
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    @Nullable
    private Boolean allowBeanDefinitionOverriding;

    @Nullable
    private Boolean allowCircularReferences;

    /** Bean factory for this context. */
    @Nullable
    private volatile DefaultListableBeanFactory beanFactory;


    /**
     * Create a new AbstractRefreshableApplicationContext with no parent.
     */
    public AbstractRefreshableApplicationContext() {
    }

    /**
     * Create a new AbstractRefreshableApplicationContext with the given parent context.
     * @param parent the parent context
     */
    public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }


    /**
     * Set whether it should be allowed to override bean definitions by registering
     * a different definition with the same name, automatically replacing the former.
     * If not, an exception will be thrown. Default is "true".
     * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     */
    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    /**
     * Set whether to allow circular references between beans - and automatically
     * try to resolve them.
     * <p>Default is "true". Turn this off to throw an exception when encountering
     * a circular reference, disallowing them completely.
     * @see DefaultListableBeanFactory#setAllowCircularReferences
     */
    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }


    /**
     * This implementation performs an actual refresh of this context's underlying
     * bean factory, shutting down the previous bean factory (if any) and
     * initializing a fresh bean factory for the next phase of the context's lifecycle.
     */
    @Override
    protected final void refreshBeanFactory() throws BeansException {
        if (hasBeanFactory()) {
            destroyBeans();
            closeBeanFactory();
        }
        try {
            DefaultListableBeanFactory beanFactory = createBeanFactory(); //day07：创建保存所有Bean定义信息的档案馆
            beanFactory.setSerializationId(getId());
            customizeBeanFactory(beanFactory);
            loadBeanDefinitions(beanFactory);
            this.beanFactory = beanFactory;
        }
        catch (IOException ex) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
        }
    }

    @Override
    protected void cancelRefresh(BeansException ex) {
        DefaultListableBeanFactory beanFactory = this.beanFactory;
        if (beanFactory != null) {
            beanFactory.setSerializationId(null);
        }
        super.cancelRefresh(ex);
    }

    @Override
    protected final void closeBeanFactory() {
        DefaultListableBeanFactory beanFactory = this.beanFactory;
        if (beanFactory != null) {
            beanFactory.setSerializationId(null);
            this.beanFactory = null;
        }
    }

    /**
     * Determine whether this context currently holds a bean factory,
     * i.e. has been refreshed at least once and not been closed yet.
     */
    protected final boolean hasBeanFactory() {
        return (this.beanFactory != null);
    }

    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        DefaultListableBeanFactory beanFactory = this.beanFactory;
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory not initialized or already closed - " +
                    "call 'refresh' before accessing beans via the ApplicationContext");
        }
        return beanFactory;
    }

    /**
     * Overridden to turn it into a no-op: With AbstractRefreshableApplicationContext,
     * {@link #getBeanFactory()} serves a strong assertion for an active context anyway.
     */
    @Override
    protected void assertBeanFactoryActive() {
    }

    /**
     * Create an internal bean factory for this context.
     * Called for each {@link #refresh()} attempt.
     * <p>The default implementation creates a
     * {@link DefaultListableBeanFactory}
     * with the {@linkplain #getInternalParentBeanFactory() internal bean factory} of this
     * context's parent as parent bean factory. Can be overridden in subclasses,
     * for example to customize DefaultListableBeanFactory's settings.
     * @return the bean factory for this context
     * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     * @see DefaultListableBeanFactory#setAllowEagerClassLoading
     * @see DefaultListableBeanFactory#setAllowCircularReferences
     * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getInternalParentBeanFactory());
    }

    /**
     * Customize the internal bean factory used by this context.
     * Called for each {@link #refresh()} attempt.
     * <p>The default implementation applies this context's
     * {@linkplain #setAllowBeanDefinitionOverriding "allowBeanDefinitionOverriding"}
     * and {@linkplain #setAllowCircularReferences "allowCircularReferences"} settings,
     * if specified. Can be overridden in subclasses to customize any of
     * {@link DefaultListableBeanFactory}'s settings.
     * @param beanFactory the newly created bean factory for this context
     * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     * @see DefaultListableBeanFactory#setAllowCircularReferences
     * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
     * @see DefaultListableBeanFactory#setAllowEagerClassLoading
     */
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }
    }

    /**
     * Load bean definitions into the given bean factory, typically through
     * delegating to one or more bean definition readers.
     * @param beanFactory the bean factory to load bean definitions into
     * @throws BeansException if parsing of the bean definitions failed
     * @throws IOException if loading of bean definition files failed
     * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws BeansException, IOException;

}
