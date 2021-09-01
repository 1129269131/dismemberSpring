package com.koala.day03refresh.Day03Refresh;

import com.koala.day03refresh.bean.Person;
import com.koala.day03refresh.springComponents.springBeans.factory.BeanFactory;
import com.koala.day03refresh.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day03refresh.springComponents.springBeans.factory.config.BeanFactoryPostProcessor;
import com.koala.day03refresh.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day03refresh.springComponents.springBeans.factory.parsing.EmptyReaderEventListener;
import com.koala.day03refresh.springComponents.springBeans.factory.parsing.ReaderEventListener;
import com.koala.day03refresh.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day03refresh.springComponents.springBeans.factory.xml.BeanDefinitionParserDelegate;
import com.koala.day03refresh.springComponents.springBeans.factory.xml.DefaultNamespaceHandlerResolver;
import com.koala.day03refresh.springComponents.springBeans.factory.xml.NamespaceHandlerResolver;
import com.koala.day03refresh.springComponents.springBeans.factory.xml.XmlBeanDefinitionReader;
import com.koala.day03refresh.springComponents.springBeans.factory.xml.XmlReaderContext;
import com.koala.day03refresh.springComponents.springContext.context.ApplicationContext;
import com.koala.day03refresh.springComponents.springContext.context.event.ContextRefreshedEvent;
import com.koala.day03refresh.springComponents.springContext.context.support.AbstractApplicationContext;
import com.koala.day03refresh.springComponents.springContext.context.support.ClassPathXmlApplicationContext;
import com.koala.day03refresh.springComponents.springContext.context.support.DefaultLifecycleProcessor;
import com.koala.day03refresh.springComponents.springContext.context.support.LiveBeansView;
import com.koala.day03refresh.springComponents.springContext.context.support.PostProcessorRegistrationDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.NullSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.NativeDetector;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create by koala on 2021-09-01
 */
public class Main {

    @Nullable
    private LifecycleProcessor lifecycleProcessor;

    //调用refresh方法
    @Test
    public void refresh() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setSerializationId("@test123");

        BeanDefinitionHolder bdHolder = this.getDocumentBeanDefinition();
        String beanName = bdHolder.getBeanName();
        beanFactory.registerBeanDefinition(beanName,bdHolder.getBeanDefinition());

        AbstractApplicationContext applicationContext = new AbstractApplicationContext() {
            @Override
            protected void refreshBeanFactory() throws BeansException, IllegalStateException {
            }

            @Override
            protected void closeBeanFactory() {
            }

            @Override
            public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
                return beanFactory;
            }
        };

        //day20：准备上下文环境 Prepare this context for refreshing.
        prepareRefresh(applicationContext);

        //day20：给容器中注册了环境信息作为单实例Bean方便后续自动装配；放了一些后置处理器处理（监听、xxAware功能） Prepare the bean factory for use in this context.
        prepareBeanFactory(applicationContext.getBeanFactory());

        ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

        StartupStep beanPostProcess = applicationStartup.start("spring.context.beans.post-process");
        //day22：【大核心】day11：工厂增强：执行所有的BeanFactory后置增强器；利用BeanFactory后置增强器对工厂进行修改或者增强,配置类会在这里进行解析。 In
        invokeBeanFactoryPostProcessors(beanFactory);

        //day22：【小核心】day12：注册所有的Bean的后置处理器 Register bean processors that intercept bean creation.
        registerBeanPostProcessors(beanFactory,applicationContext);
        beanPostProcess.end();

        // Instantiate all remaining (non-lazy-init) singletons.
        //day22：【大核心】day09：bean创建；完成 BeanFactory 初始化。（工厂里面所有的组件都好了）
        finishBeanFactoryInitialization(beanFactory);

        // Instantiate all remaining (non-lazy-init) singletons.
        //day22：【大核心】day09：bean创建；完成 BeanFactory 初始化。（工厂里面所有的组件都好了）
        finishBeanFactoryInitialization(beanFactory);

        //applicationContext.refresh();

        Person bean = applicationContext.getBean(Person.class);
        System.out.println("恭喜你，最终通过自己的容器获取到Person对象！！！");
        System.out.println(bean);
    }

    @Test
    public void prepareRefresh(AbstractApplicationContext applicationContext) {
        applicationContext.closed.set(false);
        applicationContext.active.set(true);
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        System.out.println(beanFactory);
    }

    @Test
    public void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(ClassUtils.getDefaultClassLoader());

        // Configure the bean factory with context callbacks.
        //beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this)); //day20：准备一个处理Aware接口功能的后置处理器
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class); //day20：告诉Spring先别管这些接口
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationStartupAware.class);

        //day20：注册可以解析到的依赖 BeanFactory interface not registered as resolvable type in a plain factory.
        // MessageSource registered (and found for autowiring) as a bean.
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
//        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
//        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
//        beanFactory.registerResolvableDependency(ApplicationContext.class, this);

        // Register early post-processor for detecting inner beans as ApplicationListeners.
        //beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

        //day20：注册默认组件： Register default environment beans.
        String ENVIRONMENT_BEAN_NAME = "environment";
        String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
        String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";
        String APPLICATION_STARTUP_BEAN_NAME = "applicationStartup";
        ConfigurableEnvironment environment = new StandardEnvironment();
        ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;
        if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, environment);
        }
        if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, environment.getSystemProperties());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, environment.getSystemEnvironment());
        }
        if (!beanFactory.containsLocalBean(APPLICATION_STARTUP_BEAN_NAME)) {
            beanFactory.registerSingleton(APPLICATION_STARTUP_BEAN_NAME, applicationStartup);
        }
    }

    @Test
    public void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, beanFactoryPostProcessors); //day11：执行所有的工厂增强器
    }

    @Test
    public void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory,AbstractApplicationContext applicationContext) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, applicationContext);
    }

    @Test
    public void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
        // day13：给工厂设置好 ConversionService【负责类型转换的组件服务】， Initialize conversion service for this context.
        if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
                beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
            beanFactory.setConversionService(
                    beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
        }

        // day13：注册一个默认的值解析器（"${}"）  ；Register a default embedded value resolver if no BeanFactoryPostProcessor
        // (such as a PropertySourcesPlaceholderConfigurer bean) registered any before:
        // at this point, primarily for resolution in annotation attribute values.
        ConfigurableEnvironment environment = new StandardEnvironment();
        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(strVal -> environment.resolvePlaceholders(strVal));
        }

        // day13：LoadTimeWeaverAware；aspectj：加载时织入功能【aop】。 Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
        /*String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        for (String weaverAwareName : weaverAwareNames) {
            getBean(weaverAwareName); //day13：从容器中获取组件，有则直接获取，没则进行创建
        }*/

        // Stop using the temporary ClassLoader for type matching.
        beanFactory.setTempClassLoader(null);

        // Allow for caching all bean definition metadata, not expecting further changes.
        beanFactory.freezeConfiguration();

        // Instantiate all remaining (non-lazy-init) singletons.
        //day09：初始化所有的非懒加载的单实例Bean
        beanFactory.preInstantiateSingletons();
    }

    @Test
    public void finishRefresh() {
        // Clear context-level resource caches (such as ASM metadata from scanning).
        // clearResourceCaches();

        // Initialize lifecycle processor for this context.
        initLifecycleProcessor();

        //day22：告诉LifecycleProcessor容器onRefresh Propagate refresh to lifecycle processor first.
        this.lifecycleProcessor.onRefresh();

        //day22：发布事件 Publish the final event.
        // publishEvent(new ContextRefreshedEvent(this)); //发布上下文环境刷新完成的事件

        //day22：jconsole（暴露MBean端点信息） Participate in LiveBeansView MBean, if active.
        /*if (!NativeDetector.inNativeImage()) {
            LiveBeansView.registerApplicationContext(this);
        }*/
    }

    public void initLifecycleProcessor() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";
        if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
            this.lifecycleProcessor =
                    beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
        }
        else {
            DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
            defaultProcessor.setBeanFactory(beanFactory);
            this.lifecycleProcessor = defaultProcessor;
            beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
        }
    }




    //提供beanFactory
    public DefaultListableBeanFactory getBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setSerializationId("@test123");

        AbstractApplicationContext applicationContext = new AbstractApplicationContext() {
            @Override
            protected void refreshBeanFactory() throws BeansException, IllegalStateException {
            }

            @Override
            protected void closeBeanFactory() {
            }

            @Override
            public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
                return beanFactory;
            }
        };

        return beanFactory;
    }





    //提供resource
    public Resource[] getResources() {
        ResourcePatternResolver resourceLoader = new ClassPathXmlApplicationContext();
        String location = "beans.xml";
        try {
            Resource[] resources = resourceLoader.getResources(location);
            System.out.println(resources[0].getFilename());
            return resources;
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "Could not resolve bean definition resource pattern [" + location + "]", ex);
        }
    }

    //提供document
    public Document getDocument() {
        DocumentLoader documentLoader = new DefaultDocumentLoader();
        EntityResolver entityResolver = new ResourceEntityResolver(new ClassPathXmlApplicationContext());
        Log logger = LogFactory.getLog(getClass());
        ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);
        Resource[] resources = getResources();
        Resource resource = resources[0];
        EncodedResource encodedResource = new EncodedResource(resource);
        Document doc = null;
        try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
            InputSource inputSource = new InputSource(inputStream);
            try {
                doc = documentLoader.loadDocument(inputSource, entityResolver, errorHandler,
                        3, false);
                System.out.println(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "IOException parsing XML document from " + resources[0], ex);
        }

        return doc;
    }

    //BeanDefinition
    @Test
    public BeanDefinitionHolder getDocumentBeanDefinition() {
        BeanDefinitionHolder bdHolder = null;

        Resource[] resources = getResources();
        Resource resource = resources[0];
        Document doc = getDocument();
        ProblemReporter problemReporter = new FailFastProblemReporter();
        ReaderEventListener eventListener = new EmptyReaderEventListener();
        SourceExtractor sourceExtractor = new NullSourceExtractor();
        ResourcePatternResolver resourceLoader = new ClassPathXmlApplicationContext();
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(null);
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        ClassLoader cl = resourceLoader.getClassLoader();
        NamespaceHandlerResolver namespaceHandlerResolver = new DefaultNamespaceHandlerResolver(cl);
        XmlReaderContext xmlReaderContext = new XmlReaderContext(resource, problemReporter, eventListener,
                sourceExtractor, beanDefinitionReader, namespaceHandlerResolver);
        Element root = doc.getDocumentElement();
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(xmlReaderContext);
        delegate.initDefaults(root, null);

        if (delegate.isDefaultNamespace(root)) {
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    if (delegate.isDefaultNamespace(ele)) { //day07：遍历文档中的所有节点
                        bdHolder = delegate.parseBeanDefinitionElement(ele);
                    }
                }
            }
        }

        return bdHolder;
    }
}
