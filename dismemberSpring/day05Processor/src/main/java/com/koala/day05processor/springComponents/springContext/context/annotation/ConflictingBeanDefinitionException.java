package com.koala.day05processor.springComponents.springContext.context.annotation;

/**
 * Create by koala on 2021-09-05
 */
@SuppressWarnings("serial")
class ConflictingBeanDefinitionException extends IllegalStateException {

    public ConflictingBeanDefinitionException(String message) {
        super(message);
    }

}