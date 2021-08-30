package com.koala.day01beandefinition.springComponents.springBeans.factory.xml;

import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-28
 */
@FunctionalInterface
public interface NamespaceHandlerResolver {

    /**
     * Resolve the namespace URI and return the located {@link org.springframework.beans.factory.xml.NamespaceHandler}
     * implementation.
     * @param namespaceUri the relevant namespace URI
     * @return the located {@link org.springframework.beans.factory.xml.NamespaceHandler} (may be {@code null})
     */
    @Nullable
    NamespaceHandler resolve(String namespaceUri);

}
