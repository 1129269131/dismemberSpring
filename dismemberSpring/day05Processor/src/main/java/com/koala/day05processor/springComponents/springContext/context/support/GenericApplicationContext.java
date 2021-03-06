package com.koala.day05processor.springComponents.springContext.context.support;

import com.koala.day05processor.springComponents.springBeans.factory.config.AutowireCapableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinitionCustomizer;
import com.koala.day05processor.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day05processor.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.support.RootBeanDefinition;
import com.koala.day05processor.springComponents.springContext.context.ApplicationContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Create by koala on 2021-09-05
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final DefaultListableBeanFactory beanFactory;

    @Nullable
    private ResourceLoader resourceLoader;

    private boolean customClassLoader = false;

    private final AtomicBoolean refreshed = new AtomicBoolean();


    /**
     * Create a new GenericApplicationContext.
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    /**
     * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    /**
     * Create a new GenericApplicationContext with the given parent.
     * @param parent the parent application context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }

    /**
     * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @param parent the parent application context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
        this(beanFactory);
        setParent(parent);
    }


    /**
     * Set the parent of this application context, also setting
     * the parent of the internal BeanFactory accordingly.
     * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
     */
    @Override
    public void setParent(@Nullable ApplicationContext parent) {
        super.setParent(parent);
        this.beanFactory.setParentBeanFactory(getInternalParentBeanFactory());
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        super.setApplicationStartup(applicationStartup);
        this.beanFactory.setApplicationStartup(applicationStartup);
    }

    /**
     * Set whether it should be allowed to override bean definitions by registering
     * a different definition with the same name, automatically replacing the former.
     * If not, an exception will be thrown. Default is "true".
     * @since 3.0
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     */
    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
    }

    /**
     * Set whether to allow circular references between beans - and automatically
     * try to resolve them.
     * <p>Default is "true". Turn this off to throw an exception when encountering
     * a circular reference, disallowing them completely.
     * @since 3.0
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
     */
    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.beanFactory.setAllowCircularReferences(allowCircularReferences);
    }

    /**
     * Set a ResourceLoader to use for this context. If set, the context will
     * delegate all {@code getResource} calls to the given ResourceLoader.
     * If not set, default resource loading will apply.
     * <p>The main reason to specify a custom ResourceLoader is to resolve
     * resource paths (without URL prefix) in a specific fashion.
     * The default behavior is to resolve such paths as class path locations.
     * To resolve resource paths as file system locations, specify a
     * FileSystemResourceLoader here.
     * <p>You can also pass in a full ResourcePatternResolver, which will
     * be autodetected by the context and used for {@code getResources}
     * calls as well. Else, default resource pattern matching will apply.
     * @see #getResource
     * @see org.springframework.core.io.DefaultResourceLoader
     * @see org.springframework.core.io.FileSystemResourceLoader
     * @see ResourcePatternResolver
     * @see #getResources
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    //---------------------------------------------------------------------
    // ResourceLoader / ResourcePatternResolver override if necessary
    //---------------------------------------------------------------------

    /**
     * This implementation delegates to this context's ResourceLoader if set,
     * falling back to the default superclass behavior else.
     * @see #setResourceLoader
     */
    @Override
    public Resource getResource(String location) {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getResource(location);
        }
        return super.getResource(location);
    }

    /**
     * This implementation delegates to this context's ResourceLoader if it
     * implements the ResourcePatternResolver interface, falling back to the
     * default superclass behavior else.
     * @see #setResourceLoader
     */
    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        if (this.resourceLoader instanceof ResourcePatternResolver) {
            return ((ResourcePatternResolver) this.resourceLoader).getResources(locationPattern);
        }
        return super.getResources(locationPattern);
    }

    @Override
    public void setClassLoader(@Nullable ClassLoader classLoader) {
        super.setClassLoader(classLoader);
        this.customClassLoader = true;
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null && !this.customClassLoader) {
            return this.resourceLoader.getClassLoader();
        }
        return super.getClassLoader();
    }


    //---------------------------------------------------------------------
    // Implementations of AbstractApplicationContext's template methods
    //---------------------------------------------------------------------

    /**
     * Do nothing: We hold a single internal BeanFactory and rely on callers
     * to register beans through our public methods (or the BeanFactory's).
     * @see #registerBeanDefinition
     */
    @Override
    protected final void refreshBeanFactory() throws IllegalStateException {
        if (!this.refreshed.compareAndSet(false, true)) {
            throw new IllegalStateException(
                    "GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
        }
        this.beanFactory.setSerializationId(getId());
    }

    @Override
    protected void cancelRefresh(BeansException ex) {
        this.beanFactory.setSerializationId(null);
        super.cancelRefresh(ex);
    }

    /**
     * Not much to do: We hold a single internal BeanFactory that will never
     * get released.
     */
    @Override
    protected final void closeBeanFactory() {
        this.beanFactory.setSerializationId(null);
    }

    /**
     * Return the single internal BeanFactory held by this context
     * (as ConfigurableListableBeanFactory).
     */
    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    /**
     * Return the underlying bean factory of this context,
     * available for registering bean definitions.
     * <p><b>NOTE:</b> You need to call {@link #refresh()} to initialize the
     * bean factory and its contained beans with application context semantics
     * (autodetecting BeanFactoryPostProcessors, etc).
     * @return the internal bean factory (as DefaultListableBeanFactory)
     */
    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        assertBeanFactoryActive();
        return this.beanFactory;
    }


    //---------------------------------------------------------------------
    // Implementation of BeanDefinitionRegistry
    //---------------------------------------------------------------------

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {

        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }

    @Override
    public void registerAlias(String beanName, String alias) {
        this.beanFactory.registerAlias(beanName, alias);
    }

    @Override
    public void removeAlias(String alias) {
        this.beanFactory.removeAlias(alias);
    }

    @Override
    public boolean isAlias(String beanName) {
        return this.beanFactory.isAlias(beanName);
    }


    //---------------------------------------------------------------------
    // Convenient methods for registering individual beans
    //---------------------------------------------------------------------

    /**
     * Register a bean from the given bean class, optionally providing explicit
     * constructor arguments for consideration in the autowiring process.
     * @param beanClass the class of the bean
     * @param constructorArgs custom argument values to be fed into Spring's
     * constructor resolution algorithm, resolving either all arguments or just
     * specific ones, with the rest to be resolved through regular autowiring
     * (may be {@code null} or empty)
     * @since 5.2 (since 5.0 on the AnnotationConfigApplicationContext subclass)
     */
    public <T> void registerBean(Class<T> beanClass, Object... constructorArgs) {
        registerBean(null, beanClass, constructorArgs);
    }

    /**
     * Register a bean from the given bean class, optionally providing explicit
     * constructor arguments for consideration in the autowiring process.
     * @param beanName the name of the bean (may be {@code null})
     * @param beanClass the class of the bean
     * @param constructorArgs custom argument values to be fed into Spring's
     * constructor resolution algorithm, resolving either all arguments or just
     * specific ones, with the rest to be resolved through regular autowiring
     * (may be {@code null} or empty)
     * @since 5.2 (since 5.0 on the AnnotationConfigApplicationContext subclass)
     */
    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Object... constructorArgs) {
        registerBean(beanName, beanClass, (Supplier<T>) null,
                bd -> {
                    for (Object arg : constructorArgs) {
                        bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
                    }
                });
    }

    /**
     * Register a bean from the given bean class, optionally customizing its
     * bean definition metadata (typically declared as a lambda expression).
     * @param beanClass the class of the bean (resolving a public constructor
     * to be autowired, possibly simply the default constructor)
     * @param customizers one or more callbacks for customizing the factory's
     * {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
     */
    public final <T> void registerBean(Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
        registerBean(null, beanClass, null, customizers);
    }

    /**
     * Register a bean from the given bean class, optionally customizing its
     * bean definition metadata (typically declared as a lambda expression).
     * @param beanName the name of the bean (may be {@code null})
     * @param beanClass the class of the bean (resolving a public constructor
     * to be autowired, possibly simply the default constructor)
     * @param customizers one or more callbacks for customizing the factory's
     * {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
     */
    public final <T> void registerBean(
            @Nullable String beanName, Class<T> beanClass, BeanDefinitionCustomizer... customizers) {

        registerBean(beanName, beanClass, null, customizers);
    }

    /**
     * Register a bean from the given bean class, using the given supplier for
     * obtaining a new instance (typically declared as a lambda expression or
     * method reference), optionally customizing its bean definition metadata
     * (again typically declared as a lambda expression).
     * @param beanClass the class of the bean
     * @param supplier a callback for creating an instance of the bean
     * @param customizers one or more callbacks for customizing the factory's
     * {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
     */
    public final <T> void registerBean(
            Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {

        registerBean(null, beanClass, supplier, customizers);
    }

    /**
     * Register a bean from the given bean class, using the given supplier for
     * obtaining a new instance (typically declared as a lambda expression or
     * method reference), optionally customizing its bean definition metadata
     * (again typically declared as a lambda expression).
     * <p>This method can be overridden to adapt the registration mechanism for
     * all {@code registerBean} methods (since they all delegate to this one).
     * @param beanName the name of the bean (may be {@code null})
     * @param beanClass the class of the bean
     * @param supplier a callback for creating an instance of the bean (in case
     * of {@code null}, resolving a public constructor to be autowired instead)
     * @param customizers one or more callbacks for customizing the factory's
     * {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     */
    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass,
                                 @Nullable Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {

        ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
        if (supplier != null) {
            beanDefinition.setInstanceSupplier(supplier);
        }
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(beanDefinition);
        }

        String nameToUse = (beanName != null ? beanName : beanClass.getName());
        registerBeanDefinition(nameToUse, beanDefinition);
    }


    /**
     * {@link RootBeanDefinition} marker subclass for {@code #registerBean} based
     * registrations with flexible autowiring for public constructors.
     */
    @SuppressWarnings("serial")
    private static class ClassDerivedBeanDefinition extends RootBeanDefinition {

        public ClassDerivedBeanDefinition(Class<?> beanClass) {
            super(beanClass);
        }

        public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
            super(original);
        }

        @Override
        @Nullable
        public Constructor<?>[] getPreferredConstructors() {
            Class<?> clazz = getBeanClass();
            Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
            if (primaryCtor != null) {
                return new Constructor<?>[] {primaryCtor};
            }
            Constructor<?>[] publicCtors = clazz.getConstructors();
            if (publicCtors.length > 0) {
                return publicCtors;
            }
            return null;
        }

        @Override
        public RootBeanDefinition cloneBeanDefinition() {
            return new ClassDerivedBeanDefinition(this);
        }
    }

}
