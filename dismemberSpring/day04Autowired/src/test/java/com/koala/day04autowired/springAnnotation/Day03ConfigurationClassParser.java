package com.koala.day04autowired.springAnnotation;

import com.koala.day04autowired.bean.Person;
import com.koala.day04autowired.springAnnotation.context.AnnotationConfigApplicationContext;
import com.koala.day04autowired.springComponents.springBeans.factory.BeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.annotation.AnnotatedBeanDefinition;
import com.koala.day04autowired.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.day04autowired.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day04autowired.springComponents.springBeans.factory.config.BeanFactoryPostProcessor;
import com.koala.day04autowired.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.parsing.EmptyReaderEventListener;
import com.koala.day04autowired.springComponents.springBeans.factory.parsing.ReaderEventListener;
import com.koala.day04autowired.springComponents.springBeans.factory.support.BeanDefinitionReaderUtils;
import com.koala.day04autowired.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day04autowired.springComponents.springBeans.factory.support.BeanNameGenerator;
import com.koala.day04autowired.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.xml.BeanDefinitionParserDelegate;
import com.koala.day04autowired.springComponents.springBeans.factory.xml.DefaultNamespaceHandlerResolver;
import com.koala.day04autowired.springComponents.springBeans.factory.xml.NamespaceHandlerResolver;
import com.koala.day04autowired.springComponents.springBeans.factory.xml.XmlBeanDefinitionReader;
import com.koala.day04autowired.springComponents.springBeans.factory.xml.XmlReaderContext;
import com.koala.day04autowired.springComponents.springContext.context.ApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.ApplicationContextAware;
import com.koala.day04autowired.springComponents.springContext.context.annotation.AnnotationBeanNameGenerator;
import com.koala.day04autowired.springComponents.springContext.context.annotation.AnnotationConfigUtils;
import com.koala.day04autowired.springComponents.springContext.context.annotation.AnnotationScopeMetadataResolver;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ClassPathBeanDefinitionScanner;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ConditionEvaluator;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ConfigurationClass;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ConfigurationClassParser;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ConfigurationClassUtils;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ScannedGenericBeanDefinition;
import com.koala.day04autowired.springComponents.springContext.context.annotation.ScopeMetadataResolver;
import com.koala.day04autowired.springComponents.springContext.context.support.AbstractApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.support.ApplicationContextAwareProcessor;
import com.koala.day04autowired.springComponents.springContext.context.support.ApplicationListenerDetector;
import com.koala.day04autowired.springComponents.springContext.context.support.ClassPathXmlApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.support.DefaultLifecycleProcessor;
import com.koala.day04autowired.springComponents.springContext.context.support.PostProcessorRegistrationDelegate;
import com.koala.day04autowired.springComponents.springCore.core.annotation.AnnotationAttributes;
import com.koala.day04autowired.springComponents.springCore.core.io.ProtocolResolver;
import com.koala.day04autowired.springComponents.springCore.core.io.ResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.NullSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Create by koala on 2021-09-07
 */
public class Day03ConfigurationClassParser {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;

    @Test
    public void configurationClassParserTest() throws Exception{
        String configPath = "com.koala.day04autowired.config.MainConfig";

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        org.springframework.core.io.ResourceLoader resourceLoader = applicationContext;
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        ProblemReporter problemReporter = new FailFastProblemReporter();
        ConfigurableEnvironment environment = new StandardEnvironment();

        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry)applicationContext.getBeanFactory();
        String[] candidateNames = registry.getBeanDefinitionNames(); //day21：拿到工厂所有的bean定义信息
        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
                /*if (logger.isDebugEnabled()) {
                    logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
                }*/
            }
            else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName)); //day11：将配置类加到候选集合里面，等待处理
            }
        }

        BeanNameGenerator componentScanBeanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
        MetadataReader reader = this.metadataReaderFactory.getMetadataReader(configPath);
        ConfigurationClass configClass = new ConfigurationClass(reader,"mainConfig");
//        ConfigurationClassParser parser = new ConfigurationClassParser();
        ConfigurationClassParser parser = new ConfigurationClassParser(
                this.metadataReaderFactory, problemReporter, environment,
                resourceLoader, componentScanBeanNameGenerator, registry);
//        ConfigurationClassParser.SourceClass sourceClass = asSourceClass(configurationClass, filter);

//        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
//        BeanDefinition config = (BeanDefinition)beanFactory.getBean("mainConfig");

        Predicate<String> filter = className ->
                (className.startsWith("java.lang.annotation.") || className.startsWith("org.springframework.stereotype."));

        ConfigurationClassParser.SourceClass sourceClass = parser.asSourceClass(configClass, filter);
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
                sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
        for (AnnotationAttributes componentScan : componentScans) {
            String[] basePackagesArray = componentScan.getStringArray("basePackages");
            Arrays.stream(basePackagesArray).forEach(System.out::println);

            //扫描器扫描工作
            Set<BeanDefinitionHolder> parse = parse(componentScan, configPath, registry, environment, resourceLoader, componentScanBeanNameGenerator);
            parse.stream().forEach(System.out::println);
            /*ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry,
                    componentScan.getBoolean("useDefaultFilters"), environment, resourceLoader);*/
        }

        Map<String, BeanDefinition> beanDefinitionMap = applicationContext.beanFactory.beanDefinitionMap;

        Set<String> beanDefinitionMapKeys = beanDefinitionMap.keySet();
        beanDefinitionMapKeys.stream().forEach(System.out::println);
    }

    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass,BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry,
                componentScan.getBoolean("useDefaultFilters"), environment, resourceLoader);

        Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
        boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass || generatorClass.getName() == "org.springframework.beans.factory.support.BeanNameGenerator");//20210905修改 || 及之后的，不知道为什么
        scanner.setBeanNameGenerator(useInheritedGenerator ? beanNameGenerator :
                BeanUtils.instantiateClass(generatorClass));

        ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
        if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
            scanner.setScopedProxyMode(scopedProxyMode);
        }
        else {
            Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
            //scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));//20210905注释
        }

        scanner.setResourcePattern(componentScan.getString("resourcePattern"));

        /*for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter)) {
                scanner.addIncludeFilter(typeFilter);
            }
        }*/
        /*for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter)) {
                scanner.addExcludeFilter(typeFilter);
            }
        }*/

        boolean lazyInit = componentScan.getBoolean("lazyInit");
        if (lazyInit) {
            scanner.getBeanDefinitionDefaults().setLazyInit(true);
        }

        Set<String> basePackages = new LinkedHashSet<>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(environment.resolvePlaceholders(pkg),
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            Collections.addAll(basePackages, tokenized);
        }
        for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }

        scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
            @Override
            protected boolean matchClassName(String className) {
                return declaringClass.equals(className);
            }
        });
        //return scanner.doScan(StringUtils.toStringArray(basePackages)); //day21：扫描器进行扫描
        return doScan(registry,environment,resourceLoader,beanNameGenerator,StringUtils.toStringArray(basePackages));
    }

    public Set<BeanDefinitionHolder> doScan(BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator,String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage,registry,environment,resourceLoader,beanNameGenerator); //day21：找到候选组件（我们需要扫描进来的组件）
            for (BeanDefinition candidate : candidates) {
                ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = beanNameGenerator.generateBeanName(candidate, registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                if (checkCandidate(beanName, candidate,registry)) {
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    definitionHolder =
                            AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, registry);
                    beanDefinitions.add(definitionHolder);
                    registerBeanDefinition(definitionHolder, registry);
                }
            }
        }
        return beanDefinitions;
    }

    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();
        String[] autowireCandidatePatterns = null;
        beanDefinition.applyDefaults(beanDefinitionDefaults);
        if (autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(autowireCandidatePatterns, beanName));
        }
    }

    public Set<BeanDefinition> findCandidateComponents(String basePackage,BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator) {
        CandidateComponentsIndex componentsIndex = null;
        if (componentsIndex != null && indexSupportsIncludeFilters()) {
            return addCandidateComponentsFromIndex(componentsIndex, registry,environment,resourceLoader,beanNameGenerator,basePackage);
        }
        else {
            return scanCandidateComponents(basePackage,registry,environment,resourceLoader,beanNameGenerator); //day21：扫描所有组件
        }
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage,BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        String resourcePattern = DEFAULT_RESOURCE_PATTERN;

        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage,environment) + '/' + resourcePattern;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            boolean traceEnabled = false;
            boolean debugEnabled = false;
            for (Resource resource : resources) {
                if (traceEnabled) {
                    //logger.trace("Scanning " + resource);
                }
                if (resource.isReadable()) {
                    try { //day21：生成每一个资源的元数据信息
                        MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                        if (isCandidateComponent(metadataReader, registry,environment,resourceLoader,beanNameGenerator)) { //day21：如果当前类在扫描范围
                            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                            sbd.setSource(resource);
                            if (isCandidateComponent(sbd, registry,environment,resourceLoader,beanNameGenerator)) {
                                if (debugEnabled) {
                                    //logger.debug("Identified candidate component class: " + resource);
                                }
                                candidates.add(sbd);
                            }
                            else {
                                if (debugEnabled) {
                                    //logger.debug("Ignored because not a concrete top-level class: " + resource);
                                }
                            }
                        }
                        else {
                            if (traceEnabled) {
                                //logger.trace("Ignored because not matching any filter: " + resource);
                            }
                        }
                    }
                    catch (Throwable ex) {
                        throw new BeanDefinitionStoreException(
                                "Failed to read candidate component class: " + resource, ex);
                    }
                }
                else {
                    if (traceEnabled) {
                        //logger.trace("Ignored because not readable: " + resource);
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return candidates;
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return (metadata.isIndependent() && (metadata.isConcrete() ||
                (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName()))));
    }

    protected boolean isCandidateComponent(MetadataReader metadataReader, BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator) {
        try {
            List<TypeFilter> includeFilters = new ArrayList<>();
            includeFilters.add(new AnnotationTypeFilter(Component.class));

            List<TypeFilter> excludeFilters = new ArrayList<>();

            for (TypeFilter tf : excludeFilters) {
                if (tf.match(metadataReader, getMetadataReaderFactory())) {
                    return false;
                }
            }
            for (TypeFilter tf : includeFilters) {
                if (tf.match(metadataReader, getMetadataReaderFactory())) {
                    return isConditionMatch(metadataReader,registry,environment,resourceLoader,beanNameGenerator);
                }
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return false;
    }

    private boolean isConditionMatch(MetadataReader metadataReader, BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator) {
        ConditionEvaluator conditionEvaluator = null;
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);

        if (conditionEvaluator == null) {
            conditionEvaluator =
                    new ConditionEvaluator(registry, environment, resourcePatternResolver);
        }
        return !conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        ResourcePatternResolver resourcePatternResolver = null;
        if (resourcePatternResolver == null) {
            resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return resourcePatternResolver;
    }

    protected String resolveBasePackage(String basePackage,ConfigurableEnvironment environment) {
        return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
    }

    private boolean indexSupportsIncludeFilters() {
        List<TypeFilter> includeFilters = new ArrayList<>();
        for (TypeFilter includeFilter : includeFilters) {
            if (!indexSupportsIncludeFilter(includeFilter)) {
                return false;
            }
        }
        return true;
    }

    private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, BeanDefinitionRegistry registry,ConfigurableEnvironment environment,org.springframework.core.io.ResourceLoader resourceLoader,BeanNameGenerator beanNameGenerator,String basePackage) {
        List<TypeFilter> includeFilters = new ArrayList<>();
        includeFilters.add(new AnnotationTypeFilter(Component.class));

        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            Set<String> types = new HashSet<>();
            for (TypeFilter filter : includeFilters) {
                String stereotype = extractStereotype(filter);
                if (stereotype == null) {
                    throw new IllegalArgumentException("Failed to extract stereotype from " + filter);
                }
                types.addAll(index.getCandidateTypes(basePackage, stereotype));
            }
            boolean traceEnabled = false;
            boolean debugEnabled = false;
            for (String type : types) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(type);
                if (isCandidateComponent(metadataReader, registry,environment,resourceLoader,beanNameGenerator)) {
                    ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                    sbd.setSource(metadataReader.getResource());
                    if (isCandidateComponent(sbd,registry,environment,resourceLoader,beanNameGenerator)) {
                        if (debugEnabled) {
                            //logger.debug("Using candidate component class from index: " + type);
                        }
                        candidates.add(sbd);
                    }
                    else {
                        if (debugEnabled) {
                            //logger.debug("Ignored because not a concrete top-level class: " + type);
                        }
                    }
                }
                else {
                    if (traceEnabled) {
                        //logger.trace("Ignored because matching an exclude filter: " + type);
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return candidates;
    }

    @Nullable
    private String extractStereotype(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            return ((AnnotationTypeFilter) filter).getAnnotationType().getName();
        }
        if (filter instanceof AssignableTypeFilter) {
            return ((AssignableTypeFilter) filter).getTargetType().getName();
        }
        return null;
    }

    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    private boolean indexSupportsIncludeFilter(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            Class<? extends Annotation> annotation = ((AnnotationTypeFilter) filter).getAnnotationType();
            return (AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotation) ||
                    annotation.getName().startsWith("javax."));
        }
        if (filter instanceof AssignableTypeFilter) {
            Class<?> target = ((AssignableTypeFilter) filter).getTargetType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
        }
        return false;
    }

    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition,BeanDefinitionRegistry registry) {
        try {
            if (!registry.containsBeanDefinition(beanName)) {
                return true;
            }
            BeanDefinition existingDef = registry.getBeanDefinition(beanName);
            BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
            if (originatingDef != null) {
                existingDef = originatingDef;
            }
            if (isCompatible(beanDefinition, existingDef)) {
                return false;
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return false;

        /*throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
                "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");*/
    }

    protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        return (!(existingDefinition instanceof org.springframework.context.annotation.ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
                (newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource())) ||  // scanned same file twice
                newDefinition.equals(existingDefinition));  // scanned equivalent class twice
    }

}
