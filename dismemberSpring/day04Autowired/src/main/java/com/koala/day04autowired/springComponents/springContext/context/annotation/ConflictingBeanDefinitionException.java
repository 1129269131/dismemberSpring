package com.koala.day04autowired.springComponents.springContext.context.annotation;

/**
 * Create by koala on 2021-09-05
 */
@SuppressWarnings("serial")
class ConflictingBeanDefinitionException extends IllegalStateException {

    public ConflictingBeanDefinitionException(String message) {
        super(message);
    }

}