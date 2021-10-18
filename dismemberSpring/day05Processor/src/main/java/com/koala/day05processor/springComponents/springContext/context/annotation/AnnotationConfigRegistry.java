package com.koala.day05processor.springComponents.springContext.context.annotation;

import org.springframework.context.annotation.Configuration;

/**
 * Create by koala on 2021-09-05
 */
public interface AnnotationConfigRegistry {

    /**
     * Register one or more component classes to be processed.
     * <p>Calls to {@code register} are idempotent; adding the same
     * component class more than once has no additional effect.
     * @param componentClasses one or more component classes,
     * e.g. {@link Configuration @Configuration} classes
     */
    void register(Class<?>... componentClasses);

    /**
     * Perform a scan within the specified base packages.
     * @param basePackages the packages to scan for component classes
     */
    void scan(String... basePackages);

}

