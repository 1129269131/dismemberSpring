package com.koala.day03refresh.springComponents.springBeans.factory.parsing;

import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;

/**
 * Create by koala on 2021-08-29
 */
public class EmptyReaderEventListener implements ReaderEventListener {

    @Override
    public void defaultsRegistered(DefaultsDefinition defaultsDefinition) {
        // no-op
    }

    @Override
    public void componentRegistered(ComponentDefinition componentDefinition) {
        // no-op
    }

    @Override
    public void aliasRegistered(AliasDefinition aliasDefinition) {
        // no-op
    }

    @Override
    public void importProcessed(ImportDefinition importDefinition) {
        // no-op
    }

}

