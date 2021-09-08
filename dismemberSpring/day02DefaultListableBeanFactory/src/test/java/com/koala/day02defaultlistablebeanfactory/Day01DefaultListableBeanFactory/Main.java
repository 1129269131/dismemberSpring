package com.koala.day02defaultlistablebeanfactory.Day01DefaultListableBeanFactory;

import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.parsing.EmptyReaderEventListener;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.parsing.ReaderEventListener;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.xml.BeanDefinitionParserDelegate;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.xml.DefaultNamespaceHandlerResolver;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.xml.NamespaceHandlerResolver;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.xml.XmlBeanDefinitionReader;
import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.xml.XmlReaderContext;
import com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.NullSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePatternResolver;
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

/**
 * DefaultListableBeanFactory档案馆的操作
 * Create by koala on 2021-08-30
 */
public class Main {

    //创建档案馆
    @Test
    public void createDefaultListableBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    }

    //纯粹的操作档案馆（保存单例对象/获取单例对象）
    @Test
    public void operateDefaultListableBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        String ENVIRONMENT_BEAN_NAME = "environment";
        if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, new StringBuilder("测试保存单例对象到DefaultListableBeanFactory"));
        }
        Object bean = beanFactory.getBean(ENVIRONMENT_BEAN_NAME);
        System.out.println(bean);
    }

    //档案馆 保存/获取 BeanDefinition对象
    @Test
    public void saveBeanDefinitionToDefaultListableBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinitionHolder bdHolder = this.getDocumentBeanDefinition();
        String beanName = bdHolder.getBeanName();
        beanFactory.registerBeanDefinition(beanName,bdHolder.getBeanDefinition());
        Object bean = beanFactory.getBean(beanName);
        System.out.println(bean);
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
