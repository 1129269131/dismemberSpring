package com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.event;

import com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.ApplicationContext;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
public class ContextClosedEvent extends ApplicationContextEvent {

    /**
     * Creates a new ContextClosedEvent.
     * @param source the {@code ApplicationContext} that has been closed
     * (must not be {@code null})
     */
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }

}