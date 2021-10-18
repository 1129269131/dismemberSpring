package com.koala.day05processor.springComponents.springContext.context.annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Create by koala on 2021-09-05
 */
@FunctionalInterface
public interface Condition {

    /**
     * Determine if the condition matches.
     * @param context the condition context
     * @param metadata the metadata of the {@link org.springframework.core.type.AnnotationMetadata class}
     * or {@link org.springframework.core.type.MethodMetadata method} being checked
     * @return {@code true} if the condition matches and the component can be registered,
     * or {@code false} to veto the annotated component's registration
     */
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
