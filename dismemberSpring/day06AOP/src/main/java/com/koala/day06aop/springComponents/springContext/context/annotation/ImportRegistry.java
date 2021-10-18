package com.koala.day06aop.springComponents.springContext.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-09-05
 */
interface ImportRegistry {

    @Nullable
    AnnotationMetadata getImportingClassFor(String importedClass);

    void removeImportingClass(String importingClass);

}
