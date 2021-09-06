package com.koala.day04autowired.springComponents.springContext.context.annotation;

import com.koala.day04autowired.springComponents.springBeans.factory.BeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.annotation.AnnotatedBeanDefinition;
import com.koala.day04autowired.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day04autowired.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day04autowired.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.support.AbstractBeanDefinition;
import com.koala.day04autowired.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day04autowired.springComponents.springBeans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.koala.day04autowired.springComponents.springBeans.factory.support.BeanNameGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.NativeDetector;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create by koala on 2021-09-05
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor,
        PriorityOrdered, ResourceLoaderAware, ApplicationStartupAware, BeanClassLoaderAware, EnvironmentAware {

    /**
     * A {@code BeanNameGenerator} using fully qualified class names as default bean names.
     * <p>This default for configuration-level import purposes may be overridden through
     * {@link #setBeanNameGenerator}. Note that the default for component scanning purposes
     * is a plain {@link AnnotationBeanNameGenerator#INSTANCE}, unless overridden through
     * {@link #setBeanNameGenerator} with a unified user-level bean name generator.
     * @since 5.2
     * @see #setBeanNameGenerator
     */
    public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR =
            FullyQualifiedAnnotationBeanNameGenerator.INSTANCE;

    private static final String IMPORT_REGISTRY_BEAN_NAME =
            org.springframework.context.annotation.ConfigurationClassPostProcessor.class.getName() + ".importRegistry";


    private final Log logger = LogFactory.getLog(getClass());

    private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();

    private ProblemReporter problemReporter = new FailFastProblemReporter();

    @Nullable
    private Environment environment;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private boolean setMetadataReaderFactoryCalled = false;

    private final Set<Integer> registriesPostProcessed = new HashSet<>();

    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    @Nullable
    private ConfigurationClassBeanDefinitionReader reader;

    private boolean localBeanNameGeneratorSet = false;

    /* Using short class names as default bean names by default. */
    private BeanNameGenerator componentScanBeanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

    /* Using fully qualified class names as default bean names by default. */
    private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;

    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;  // within PriorityOrdered
    }

    /**
     * Set the {@link SourceExtractor} to use for generated bean definitions
     * that correspond to {@link Bean} factory methods.
     */
    public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
        this.sourceExtractor = (sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor());
    }

    /**
     * Set the {@link ProblemReporter} to use.
     * <p>Used to register any problems detected with {@link Configuration} or {@link Bean}
     * declarations. For instance, an @Bean method marked as {@code final} is illegal
     * and would be reported as a problem. Defaults to {@link FailFastProblemReporter}.
     */
    public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
        this.problemReporter = (problemReporter != null ? problemReporter : new FailFastProblemReporter());
    }

    /**
     * Set the {@link MetadataReaderFactory} to use.
     * <p>Default is a {@link CachingMetadataReaderFactory} for the specified
     * {@linkplain #setBeanClassLoader bean class loader}.
     */
    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }

    /**
     * Set the {@link BeanNameGenerator} to be used when triggering component scanning
     * from {@link Configuration} classes and when registering {@link Import}'ed
     * configuration classes. The default is a standard {@link AnnotationBeanNameGenerator}
     * for scanned components (compatible with the default in {@link ClassPathBeanDefinitionScanner})
     * and a variant thereof for imported configuration classes (using unique fully-qualified
     * class names instead of standard component overriding).
     * <p>Note that this strategy does <em>not</em> apply to {@link Bean} methods.
     * <p>This setter is typically only appropriate when configuring the post-processor as a
     * standalone bean definition in XML, e.g. not using the dedicated {@code AnnotationConfig*}
     * application contexts or the {@code <context:annotation-config>} element. Any bean name
     * generator specified against the application context will take precedence over any set here.
     * @since 3.1.1
     * @see org.springframework.context.annotation.AnnotationConfigUtils#CONFIGURATION_BEAN_NAME_GENERATOR
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
        this.localBeanNameGeneratorSet = true;
        this.componentScanBeanNameGenerator = beanNameGenerator;
        this.importBeanNameGenerator = beanNameGenerator;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    /**
     * Derive further bean definitions from the configuration classes in the registry.
     */
    @Override //day11：把配置类中所有bean的定义信息导入进来。
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called on this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(registryId);

        processConfigBeanDefinitions(registry); //day21：处理配置的BeanDefinition信息
    }

    /**
     * Prepare the Configuration classes for servicing bean requests at runtime
     * by replacing them with CGLIB-enhanced subclasses.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(factoryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called on this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add(factoryId);
        if (!this.registriesPostProcessed.contains(factoryId)) {
            // BeanDefinitionRegistryPostProcessor hook apparently not supported...
            // Simply call processConfigurationClasses lazily at this point then.
            processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
        }

        enhanceConfigurationClasses(beanFactory);
        beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
    }

    /**
     * Build and validate a configuration model based on the registry of
     * {@link Configuration} classes.
     */
    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames(); //day21：拿到工厂所有的bean定义信息

        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
                }
            }
            else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName)); //day11：将配置类加到候选集合里面，等待处理
            }
        }

        // Return immediately if no @Configuration classes were found
        if (configCandidates.isEmpty()) {
            return;
        }

        // day11：对所有的配置类进行排序，Sort by previously determined @Order value, if applicable
        configCandidates.sort((bd1, bd2) -> {
            int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
            int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
            return Integer.compare(i1, i2);
        });

        // Detect any custom bean name generation strategy supplied through the enclosing application context
        SingletonBeanRegistry sbr = null;
        if (registry instanceof SingletonBeanRegistry) {
            sbr = (SingletonBeanRegistry) registry;
            if (!this.localBeanNameGeneratorSet) {
                BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(
                        AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR); //day11：getBean--getSingleton，获取创建一个internalConfigurationBeanNameGenerator来用来生成配置类的名字
                if (generator != null) {
                    this.componentScanBeanNameGenerator = generator;
                    this.importBeanNameGenerator = generator;
                }
            }
        }

        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }

        //day11：由ConfigurationClassParser解析每一个配置类 Parse each @Configuration class
        ConfigurationClassParser parser = new ConfigurationClassParser(
                this.metadataReaderFactory, this.problemReporter, this.environment,
                this.resourceLoader, this.componentScanBeanNameGenerator, registry);

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
        do {
            StartupStep processConfig = this.applicationStartup.start("spring.context.config-classes.parse");
            parser.parse(candidates); //day21：解析配置类;所有需要扫描进来的组件的BeanDefinition信息都已经完成
            parser.validate();

            Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);
            //day21：每一个组件都可以当配置类，@Import之类都能进行处理
            // Read the model and create bean definitions based on its content
            if (this.reader == null) {
                this.reader = new ConfigurationClassBeanDefinitionReader(
                        registry, this.sourceExtractor, this.resourceLoader, this.environment,
                        this.importBeanNameGenerator, parser.getImportRegistry());
            }
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);
            processConfig.tag("classCount", () -> String.valueOf(configClasses.size())).end();

            candidates.clear();
            if (registry.getBeanDefinitionCount() > candidateNames.length) {
                String[] newCandidateNames = registry.getBeanDefinitionNames();
                Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
                Set<String> alreadyParsedClasses = new HashSet<>();
                for (ConfigurationClass configurationClass : alreadyParsed) {
                    alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
                }
                for (String candidateName : newCandidateNames) {
                    if (!oldCandidateNames.contains(candidateName)) {
                        BeanDefinition bd = registry.getBeanDefinition(candidateName);
                        if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
                                !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                            candidates.add(new BeanDefinitionHolder(bd, candidateName));
                        }
                    }
                }
                candidateNames = newCandidateNames;
            }
        }
        while (!candidates.isEmpty());

        // Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
        if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
            sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
        }

        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            // Clear cache in externally provided MetadataReaderFactory; this is a no-op
            // for a shared cache since it'll be cleared by the ApplicationContext.
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }

    /**
     * Post-processes a BeanFactory in search of Configuration class BeanDefinitions;
     * any candidates are then enhanced by a {@link ConfigurationClassEnhancer}.
     * Candidate status is determined by BeanDefinition attribute metadata.
     * @see ConfigurationClassEnhancer
     */
    public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        StartupStep enhanceConfigClasses = this.applicationStartup.start("spring.context.config-classes.enhance");
        Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            Object configClassAttr = beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
            AnnotationMetadata annotationMetadata = null;
            MethodMetadata methodMetadata = null;
            if (beanDef instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDef;
                annotationMetadata = annotatedBeanDefinition.getMetadata();
                methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            }
            if ((configClassAttr != null || methodMetadata != null) && beanDef instanceof AbstractBeanDefinition) {
                // Configuration class (full or lite) or a configuration-derived @Bean method
                // -> eagerly resolve bean class at this point, unless it's a 'lite' configuration
                // or component class without @Bean methods.
                AbstractBeanDefinition abd = (AbstractBeanDefinition) beanDef;
                if (!abd.hasBeanClass()) {
                    boolean liteConfigurationCandidateWithoutBeanMethods =
                            (ConfigurationClassUtils.CONFIGURATION_CLASS_LITE.equals(configClassAttr) &&
                                    annotationMetadata != null && !ConfigurationClassUtils.hasBeanMethods(annotationMetadata));
                    if (!liteConfigurationCandidateWithoutBeanMethods) {
                        try {
                            abd.resolveBeanClass(this.beanClassLoader);
                        }
                        catch (Throwable ex) {
                            throw new IllegalStateException(
                                    "Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
                        }
                    }
                }
            }
            if (ConfigurationClassUtils.CONFIGURATION_CLASS_FULL.equals(configClassAttr)) {
                if (!(beanDef instanceof AbstractBeanDefinition)) {
                    throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" +
                            beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
                }
                else if (logger.isInfoEnabled() && beanFactory.containsSingleton(beanName)) {
                    logger.info("Cannot enhance @Configuration bean definition '" + beanName +
                            "' since its singleton instance has been created too early. The typical cause " +
                            "is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor " +
                            "return type: Consider declaring such methods as 'static'.");
                }
                configBeanDefs.put(beanName, (AbstractBeanDefinition) beanDef);
            }
        }
        if (configBeanDefs.isEmpty() || NativeDetector.inNativeImage()) {
            // nothing to enhance -> return immediately
            enhanceConfigClasses.end();
            return;
        }

        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            AbstractBeanDefinition beanDef = entry.getValue();
            // If a @Configuration class gets proxied, always proxy the target class
            beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            // Set enhanced subclass of the user-specified bean class
            Class<?> configClass = beanDef.getBeanClass();
            Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
            if (configClass != enhancedClass) {
                if (logger.isTraceEnabled()) {
                    logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with " +
                            "enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                }
                beanDef.setBeanClass(enhancedClass);
            }
        }
        enhanceConfigClasses.tag("classCount", () -> String.valueOf(configBeanDefs.keySet().size())).end();
    }


    private static class ImportAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

        private final BeanFactory beanFactory;

        public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public PropertyValues postProcessProperties(@Nullable PropertyValues pvs, Object bean, String beanName) {
            // Inject the BeanFactory before AutowiredAnnotationBeanPostProcessor's
            // postProcessProperties method attempts to autowire other configuration beans.
            if (bean instanceof ConfigurationClassEnhancer.EnhancedConfiguration) {
                ((ConfigurationClassEnhancer.EnhancedConfiguration) bean).setBeanFactory(this.beanFactory);
            }
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof ImportAware) {
                ImportRegistry ir = this.beanFactory.getBean(IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
                AnnotationMetadata importingClass = ir.getImportingClassFor(ClassUtils.getUserClass(bean).getName());
                if (importingClass != null) {
                    ((ImportAware) bean).setImportMetadata(importingClass);
                }
            }
            return bean;
        }
    }

}
