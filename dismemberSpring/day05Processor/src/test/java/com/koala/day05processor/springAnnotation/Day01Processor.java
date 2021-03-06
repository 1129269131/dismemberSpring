package com.koala.day05processor.springAnnotation;

import com.koala.day05processor.bean.Person;
import com.koala.day05processor.springAnnotation.context.AnnotationConfigApplicationContext;
import com.koala.day05processor.springComponents.springBeans.factory.BeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day05processor.springComponents.springBeans.factory.config.BeanFactoryPostProcessor;
import com.koala.day05processor.springComponents.springBeans.factory.config.ConfigurableListableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.parsing.EmptyReaderEventListener;
import com.koala.day05processor.springComponents.springBeans.factory.parsing.ReaderEventListener;
import com.koala.day05processor.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day05processor.springComponents.springBeans.factory.xml.BeanDefinitionParserDelegate;
import com.koala.day05processor.springComponents.springBeans.factory.xml.DefaultNamespaceHandlerResolver;
import com.koala.day05processor.springComponents.springBeans.factory.xml.NamespaceHandlerResolver;
import com.koala.day05processor.springComponents.springBeans.factory.xml.XmlBeanDefinitionReader;
import com.koala.day05processor.springComponents.springBeans.factory.xml.XmlReaderContext;
import com.koala.day05processor.springComponents.springContext.context.ApplicationContext;
import com.koala.day05processor.springComponents.springContext.context.ApplicationContextAware;
import com.koala.day05processor.springComponents.springContext.context.support.AbstractApplicationContext;
import com.koala.day05processor.springComponents.springContext.context.support.ApplicationContextAwareProcessor;
import com.koala.day05processor.springComponents.springContext.context.support.ApplicationListenerDetector;
import com.koala.day05processor.springComponents.springContext.context.support.ClassPathXmlApplicationContext;
import com.koala.day05processor.springComponents.springContext.context.support.DefaultLifecycleProcessor;
import com.koala.day05processor.springComponents.springContext.context.support.PostProcessorRegistrationDelegate;
import com.koala.day05processor.springComponents.springCore.core.io.ProtocolResolver;
import com.koala.day05processor.springComponents.springCore.core.io.ResourceLoader;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by koala on 2021-09-12
 */
public class Day01Processor {

    @Nullable
    private LifecycleProcessor lifecycleProcessor;

    //??????refresh??????
    @Test
    public void refresh() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        //day20???????????????????????? Prepare this context for refreshing.
        prepareRefresh(applicationContext);

        //day20???????????????????????????????????????????????????Bean????????????????????????????????????????????????????????????????????????xxAware????????? Prepare the bean factory for use in this context.
        prepareBeanFactory(applicationContext);

        ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

        StartupStep beanPostProcess = applicationStartup.start("spring.context.beans.post-process");
        //day22??????????????????day11?????????????????????????????????BeanFactory????????????????????????BeanFactory????????????????????????????????????????????????,???????????????????????????????????? In
        invokeBeanFactoryPostProcessors(applicationContext.getBeanFactory());

        //day22??????????????????day12??????????????????Bean?????????????????? Register bean processors that intercept bean creation.
        registerBeanPostProcessors(applicationContext.getBeanFactory(),applicationContext);
        beanPostProcess.end();

        // ???????????????????????????????????? + processor????????????????????????????????????
        // Instantiate all remaining (non-lazy-init) singletons.
        //day22??????????????????day09???bean??????????????? BeanFactory ??????????????????????????????????????????????????????
        finishBeanFactoryInitialization(applicationContext.getBeanFactory());

        //applicationContext.refresh();

        Person bean = applicationContext.getBean(Person.class);
        System.out.println("?????????????????????????????????annotation???????????????Person???????????????");
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
    public void prepareBeanFactory(AbstractApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.setBeanClassLoader(ClassUtils.getDefaultClassLoader());

        // Configure the bean factory with context callbacks.
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(applicationContext)); //day20?????????????????????Aware??????????????????????????????
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class); //day20?????????Spring?????????????????????
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationStartupAware.class);

        //day20????????????????????????????????? BeanFactory interface not registered as resolvable type in a plain factory.
        // MessageSource registered (and found for autowiring) as a bean.
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, applicationContext);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, applicationContext);
        beanFactory.registerResolvableDependency(ApplicationContext.class, applicationContext);

        // Register early post-processor for detecting inner beans as ApplicationListeners.
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));

        //day20???????????????????????? Register default environment beans.
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
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, beanFactoryPostProcessors); //day11?????????????????????????????????
    }

    @Test
    public void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory,AbstractApplicationContext applicationContext) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, applicationContext);
    }

    @Test
    public void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
        // day13????????????????????? ConversionService?????????????????????????????????????????? Initialize conversion service for this context.
        if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
                beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
            beanFactory.setConversionService(
                    beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
        }

        // day13???????????????????????????????????????"${}"???  ???Register a default embedded value resolver if no BeanFactoryPostProcessor
        // (such as a PropertySourcesPlaceholderConfigurer bean) registered any before:
        // at this point, primarily for resolution in annotation attribute values.
        ConfigurableEnvironment environment = new StandardEnvironment();
        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(strVal -> environment.resolvePlaceholders(strVal));
        }

        // day13???LoadTimeWeaverAware???aspectj???????????????????????????aop?????? Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
        /*String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        for (String weaverAwareName : weaverAwareNames) {
            getBean(weaverAwareName); //day13?????????????????????????????????????????????????????????????????????
        }*/

        // Stop using the temporary ClassLoader for type matching.
        beanFactory.setTempClassLoader(null);

        // Allow for caching all bean definition metadata, not expecting further changes.
        beanFactory.freezeConfiguration();

        // Instantiate all remaining (non-lazy-init) singletons.
        //day09?????????????????????????????????????????????Bean
        beanFactory.preInstantiateSingletons();
    }

    @Test
    public void finishRefresh() {
        // Clear context-level resource caches (such as ASM metadata from scanning).
        // clearResourceCaches();

        // Initialize lifecycle processor for this context.
        initLifecycleProcessor();

        //day22?????????LifecycleProcessor??????onRefresh Propagate refresh to lifecycle processor first.
        this.lifecycleProcessor.onRefresh();

        //day22??????????????? Publish the final event.
        // publishEvent(new ContextRefreshedEvent(this)); //??????????????????????????????????????????

        //day22???jconsole?????????MBean??????????????? Participate in LiveBeansView MBean, if active.
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




    //??????beanFactory
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
            public void addProtocolResolver(ProtocolResolver resolver) {

            }

            @Override
            public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
                return beanFactory;
            }
        };

        return beanFactory;
    }





    //??????resource
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

    //??????document
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
                    if (delegate.isDefaultNamespace(ele)) { //day07?????????????????????????????????
                        bdHolder = delegate.parseBeanDefinitionElement(ele);
                    }
                }
            }
        }

        return bdHolder;
    }

}
