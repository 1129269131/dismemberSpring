package com.koala.day05processor.springAnnotation.context;

import com.koala.day05processor.config.MainConfig;
import com.koala.day05processor.springComponents.springBeans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinitionCustomizer;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day05processor.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day05processor.springComponents.springBeans.factory.support.BeanNameGenerator;
import com.koala.day05processor.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.support.RootBeanDefinition;
import com.koala.day05processor.springComponents.springContext.context.annotation.AnnotatedGenericBeanDefinition;
import com.koala.day05processor.springComponents.springContext.context.annotation.AnnotationBeanNameGenerator;
import com.koala.day05processor.springComponents.springContext.context.annotation.AnnotationConfigRegistry;
import com.koala.day05processor.springComponents.springContext.context.annotation.AnnotationConfigUtils;
import com.koala.day05processor.springComponents.springContext.context.annotation.AnnotationScopeMetadataResolver;
import com.koala.day05processor.springComponents.springContext.context.annotation.ConfigurationClassPostProcessor;
import com.koala.day05processor.springComponents.springContext.context.annotation.ScopeMetadataResolver;
import com.koala.day05processor.springComponents.springContext.context.support.AbstractApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Create by koala on 2021-09-08
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext implements AnnotationConfigRegistry, BeanDefinitionRegistry {

    /**
     * The bean name of the internally managed Configuration annotation processor.
     */
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalConfigurationAnnotationProcessor";

    /**
     * The bean name of the internally managed BeanNameGenerator for use when processing
     * {@link Configuration} classes. Set by {@link org.springframework.context.annotation.AnnotationConfigApplicationContext}
     * and {@code AnnotationConfigWebApplicationContext} during bootstrap in order to make
     * any custom name generation strategy available to the underlying
     * {@link ConfigurationClassPostProcessor}.
     * @since 3.1.1
     */
    public static final String CONFIGURATION_BEAN_NAME_GENERATOR =
            "org.springframework.context.annotation.internalConfigurationBeanNameGenerator";

    /**
     * The bean name of the internally managed Autowired annotation processor.
     */
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";

    /**
     * The bean name of the internally managed Required annotation processor.
     * @deprecated as of 5.1, since no Required processor is registered by default anymore
     */
    @Deprecated
    public static final String REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalRequiredAnnotationProcessor";

    /**
     * The bean name of the internally managed JSR-250 annotation processor.
     */
    public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalCommonAnnotationProcessor";

    /**
     * The bean name of the internally managed JPA annotation processor.
     */
    public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalPersistenceAnnotationProcessor";

    private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME =
            "org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";

    /**
     * The bean name of the internally managed @EventListener annotation processor.
     */
    public static final String EVENT_LISTENER_PROCESSOR_BEAN_NAME =
            "org.springframework.context.event.internalEventListenerProcessor";

    /**
     * The bean name of the internally managed EventListenerFactory.
     */
    public static final String EVENT_LISTENER_FACTORY_BEAN_NAME =
            "org.springframework.context.event.internalEventListenerFactory";

    //private final AnnotatedBeanDefinitionReader reader;

    public DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    public AnnotationConfigApplicationContext() {
        this.beanFactory.setSerializationId("@test123");

        StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
        //this.reader = new AnnotatedBeanDefinitionReader(this);

        //day19??????????????????  ?????????????????????
        if (!this.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            def.setSource(null);
            this.beanFactory.registerBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME, def);
        }

        //day19???????????????????????????????????????
        if (!this.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            def.setSource(null);
            this.beanFactory.registerBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, def);
        }

        //day19???????????????JSR-250?????????
        // Check for JSR-250 support, and if present add the CommonAnnotationBeanPostProcessor.
        /*if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        }*/

        // Check for JPA support, and if present add the PersistenceAnnotationBeanPostProcessor.
        /*if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition();
            try {
                def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
                        org.springframework.context.annotation.AnnotationConfigUtils.class.getClassLoader()));
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException(
                        "Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
            }
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
        }*/

        if (!this.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
            def.setSource(null);
            this.beanFactory.registerBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME, def);
        }

        if (!this.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
            def.setSource(null);
            this.beanFactory.registerBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME, def);
        }

        // ?????????????????????????????? --???Spring?????? AnnotationConfigApplicationContext.java ?????? register(componentClasses); ?????????
        BeanDefinitionHolder beanDefinitionHolder = this.doRegisterBean(MainConfig.class, null, null, null, null);
        this.beanFactory.registerBeanDefinition("mainConfig", beanDefinitionHolder.getBeanDefinition());

        createAnnotatedBeanDefReader.end();
    }

    @Override
    public void register(Class<?>... componentClasses) {

    }

    @Override
    public void scan(String... basePackages) {

    }

    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {

    }

    @Override
    protected void closeBeanFactory() {

    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        /*DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setSerializationId("@test123");*/
        return this.beanFactory;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {

    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return false;
    }

    @Override
    public void registerAlias(String s, String s1) {

    }

    @Override
    public void removeAlias(String s) {

    }

    @Override
    public boolean isAlias(String s) {
        return false;
    }

    /**
     * ???Class??????BeanDefinitionHolder????????????
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     * @param beanClass the class of the bean
     * @param name an explicit name for the bean
     * @param qualifiers specific qualifier annotations to consider, if any,
     * in addition to qualifiers at the bean class level
     * @param supplier a callback for creating an instance of the bean
     * (may be {@code null})
     * @param customizers one or more callbacks for customizing the factory's
     * {@link org.springframework.beans.factory.config.BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     */
    private <T> BeanDefinitionHolder doRegisterBean(Class<T> beanClass, @Nullable String name,
                                    @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
                                    @Nullable BeanDefinitionCustomizer[] customizers) {

        BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

        ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
        /*if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }*/

        abd.setInstanceSupplier(supplier);
        ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = (name != null ? name : beanNameGenerator.generateBeanName(abd, this));
        //day19????????????????????????BeanDefinition
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        if (qualifiers != null) {
            for (Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                }
                else if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                }
                else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }
        if (customizers != null) {
            for (BeanDefinitionCustomizer customizer : customizers) {
                customizer.customize(abd);
            }
        }

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this);
        return definitionHolder;
    }
}
