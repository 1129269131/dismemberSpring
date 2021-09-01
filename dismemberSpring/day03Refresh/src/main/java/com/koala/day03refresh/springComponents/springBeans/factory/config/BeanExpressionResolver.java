package com.koala.day03refresh.springComponents.springBeans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-29
 */
public interface BeanExpressionResolver {

    /**
     * Evaluate the given value as an expression, if applicable;
     * return the value as-is otherwise.
     * @param value the value to check
     * @param evalContext the evaluation context
     * @return the resolved value (potentially the given value as-is)
     * @throws BeansException if evaluation failed
     */
    @Nullable
    Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException;

}
