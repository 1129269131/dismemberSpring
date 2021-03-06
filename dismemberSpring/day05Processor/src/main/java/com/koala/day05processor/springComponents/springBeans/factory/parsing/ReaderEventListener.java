package com.koala.day05processor.springComponents.springBeans.factory.parsing;

import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;

import java.util.EventListener;

public interface ReaderEventListener extends EventListener {

    /**
     * Notification that the given defaults has been registered.
     * @param defaultsDefinition a descriptor for the defaults
     * @see org.springframework.beans.factory.xml.DocumentDefaultsDefinition
     */
    void defaultsRegistered(DefaultsDefinition defaultsDefinition);

    /**
     * Notification that the given component has been registered.
     * @param componentDefinition a descriptor for the new component
     * @see BeanComponentDefinition
     */
    void componentRegistered(ComponentDefinition componentDefinition);

    /**
     * Notification that the given alias has been registered.
     * @param aliasDefinition a descriptor for the new alias
     */
    void aliasRegistered(AliasDefinition aliasDefinition);

    /**
     * Notification that the given import has been processed.
     * @param importDefinition a descriptor for the import
     */
    void importProcessed(ImportDefinition importDefinition);

}