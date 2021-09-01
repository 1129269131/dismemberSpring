package com.koala.day03refresh.springComponents.springBeans.factory.support;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
class ImplicitlyAppearedSingletonException extends IllegalStateException {

    public ImplicitlyAppearedSingletonException() {
        super("About-to-be-created singleton instance implicitly appeared through the " +
                "creation of the factory bean that its bean definition points to");
    }

}
