package com.koala.beandefinition.springComponents.springBeans.factory.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by koala on 2021-08-28
 */
public class ReplaceOverride extends MethodOverride {

    private final String methodReplacerBeanName;

    private final List<String> typeIdentifiers = new ArrayList<>();


    /**
     * Construct a new ReplaceOverride.
     * @param methodName the name of the method to override
     * @param methodReplacerBeanName the bean name of the MethodReplacer
     */
    public ReplaceOverride(String methodName, String methodReplacerBeanName) {
        super(methodName);
        Assert.notNull(methodReplacerBeanName, "Method replacer bean name must not be null");
        this.methodReplacerBeanName = methodReplacerBeanName;
    }


    /**
     * Return the name of the bean implementing MethodReplacer.
     */
    public String getMethodReplacerBeanName() {
        return this.methodReplacerBeanName;
    }

    /**
     * Add a fragment of a class string, like "Exception"
     * or "java.lang.Exc", to identify a parameter type.
     * @param identifier a substring of the fully qualified class name
     */
    public void addTypeIdentifier(String identifier) {
        this.typeIdentifiers.add(identifier);
    }


    @Override
    public boolean matches(Method method) {
        if (!method.getName().equals(getMethodName())) {
            return false;
        }
        if (!isOverloaded()) {
            // Not overloaded: don't worry about arg type matching...
            return true;
        }
        // If we get here, we need to insist on precise argument matching...
        if (this.typeIdentifiers.size() != method.getParameterCount()) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < this.typeIdentifiers.size(); i++) {
            String identifier = this.typeIdentifiers.get(i);
            if (!parameterTypes[i].getName().contains(identifier)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof ReplaceOverride) || !super.equals(other)) {
            return false;
        }
        ReplaceOverride that = (ReplaceOverride) other;
        return (ObjectUtils.nullSafeEquals(this.methodReplacerBeanName, that.methodReplacerBeanName) &&
                ObjectUtils.nullSafeEquals(this.typeIdentifiers, that.typeIdentifiers));
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.methodReplacerBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.typeIdentifiers);
        return hashCode;
    }

    @Override
    public String toString() {
        return "Replace override for method '" + getMethodName() + "'";
    }

}