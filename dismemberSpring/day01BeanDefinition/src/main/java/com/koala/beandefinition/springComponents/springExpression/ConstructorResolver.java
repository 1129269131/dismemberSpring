package com.koala.beandefinition.springComponents.springExpression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Create by koala on 2021-08-29
 */
@FunctionalInterface
public interface ConstructorResolver {

    /**
     * Within the supplied context determine a suitable constructor on the supplied type
     * that can handle the specified arguments. Return a ConstructorExecutor that can be
     * used to invoke that constructor (or {@code null} if no constructor could be found).
     * @param context the current evaluation context
     * @param typeName the type upon which to look for the constructor
     * @param argumentTypes the arguments that the constructor must be able to handle
     * @return a ConstructorExecutor that can invoke the constructor, or null if non found
     */
    @Nullable
    ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
            throws AccessException;

}
