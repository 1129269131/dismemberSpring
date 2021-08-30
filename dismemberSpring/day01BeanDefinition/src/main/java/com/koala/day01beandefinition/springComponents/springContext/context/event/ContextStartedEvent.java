package com.koala.day01beandefinition.springComponents.springContext.context.event;

import com.koala.day01beandefinition.springComponents.springContext.context.ApplicationContext;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
public class ContextStartedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextStartedEvent.
     * @param source the {@code ApplicationContext} that has been started
     * (must not be {@code null})
     */
    public ContextStartedEvent(ApplicationContext source) {
        super(source);
    }

}
