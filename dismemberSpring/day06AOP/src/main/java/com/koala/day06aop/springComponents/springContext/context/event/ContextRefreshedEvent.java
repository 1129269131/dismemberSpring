package com.koala.day06aop.springComponents.springContext.context.event;

import com.koala.day06aop.springComponents.springContext.context.ApplicationContext;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
public class ContextRefreshedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextRefreshedEvent.
     * @param source the {@code ApplicationContext} that has been initialized
     * or refreshed (must not be {@code null})
     */
    public ContextRefreshedEvent(ApplicationContext source) {
        super(source);
    }

}
