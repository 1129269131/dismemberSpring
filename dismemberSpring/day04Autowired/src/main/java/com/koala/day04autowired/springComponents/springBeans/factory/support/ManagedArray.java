package com.koala.day04autowired.springComponents.springBeans.factory.support;

import org.springframework.beans.factory.support.ManagedList;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
public class ManagedArray extends ManagedList<Object> {

    /** Resolved element type for runtime creation of the target array. */
    @Nullable
    volatile Class<?> resolvedElementType;


    /**
     * Create a new managed array placeholder.
     * @param elementTypeName the target element type as a class name
     * @param size the size of the array
     */
    public ManagedArray(String elementTypeName, int size) {
        super(size);
        Assert.notNull(elementTypeName, "elementTypeName must not be null");
        setElementTypeName(elementTypeName);
    }

}
