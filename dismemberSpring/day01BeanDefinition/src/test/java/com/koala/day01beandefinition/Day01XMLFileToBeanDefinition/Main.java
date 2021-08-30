package com.koala.day01beandefinition.Day01XMLFileToBeanDefinition;

import com.koala.day01beandefinition.springComponents.springBeans.factory.parsing.EmptyReaderEventListener;
import com.koala.day01beandefinition.springComponents.springBeans.factory.parsing.ReaderEventListener;
import com.koala.day01beandefinition.springComponents.springBeans.factory.support.DefaultListableBeanFactory;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.BeanDefinitionDocumentReader;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.DefaultBeanDefinitionDocumentReader;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.DefaultNamespaceHandlerResolver;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.NamespaceHandlerResolver;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.XmlBeanDefinitionReader;
import com.koala.day01beandefinition.springComponents.springBeans.factory.xml.XmlReaderContext;
import com.koala.day01beandefinition.springComponents.springContext.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
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
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 探究XML文件转化为BeanDefinition的过程
 * Create by koala on 2021-08-29
 */
public class Main {

    //解析xml文件转化为Resource.java类的过程
    @Test
    public void test01() {
        ResourcePatternResolver resourceLoader = new ClassPathXmlApplicationContext();
        String location = "beans.xml";
        try {
            Resource[] resources = resourceLoader.getResources(location);
            System.out.println(resources[0].getFilename());
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "Could not resolve bean definition resource pattern [" + location + "]", ex);
        }
    }

    //利用dom解析工具把xml变成Document
    @Test
    public void test02() {
        DocumentLoader documentLoader = new DefaultDocumentLoader();
        EntityResolver entityResolver = new ResourceEntityResolver(new ClassPathXmlApplicationContext());
        Log logger = LogFactory.getLog(getClass());
        ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);
        Resource[] resources = getResources();
        Resource resource = resources[0];
        EncodedResource encodedResource = new EncodedResource(resource);
        try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
            InputSource inputSource = new InputSource(inputStream);
            try {
                Document doc = documentLoader.loadDocument(inputSource, entityResolver, errorHandler,
                        3, false);
                System.out.println(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "IOException parsing XML document from " +  resources[0], ex);
        }
    }

    //将Document里面的对象解析成BeanDefinition对象
    @Test
    public void registerBeanDefinitions() {
        BeanDefinitionDocumentReader documentReader = BeanUtils.instantiateClass(DefaultBeanDefinitionDocumentReader.class);
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
        documentReader.registerBeanDefinitions(doc, xmlReaderContext);
    }

    //提供resource
    public Resource[] getResources() {
        ResourcePatternResolver resourceLoader = new ClassPathXmlApplicationContext();
        String location = "beans.xml";
        try {
            Resource[] resources = resourceLoader.getResources(location);
            System.out.println(resources[0].getFilename());
            return resources;
        }
        catch (IOException ex) {
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
                    "IOException parsing XML document from " +  resources[0], ex);
        }

        return doc;
    }

}
