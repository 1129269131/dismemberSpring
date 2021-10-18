package com.koala.day06aop.springComponents.springBeans.factory.support;

import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-29
 */
final class NullBean {

    NullBean() {
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return (this == obj || obj == null);
    }

    @Override
    public int hashCode() {
        return NullBean.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }

}
