package com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.w3c.dom.Document;

/**
 * Create by koala on 2021-08-28
 */
public interface BeanDefinitionDocumentReader {

    void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
            throws BeanDefinitionStoreException;

}
