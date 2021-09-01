package com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.event;

import com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.ApplicationContext;
import com.koala.day02defaultlistablebeanfactory.springComponents.springContext.context.event.ApplicationContextEvent;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
public class ContextStoppedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextStoppedEvent.
     * @param source the {@code ApplicationContext} that has been stopped
     * (must not be {@code null})
     */
    public ContextStoppedEvent(ApplicationContext source) {
        super(source);
    }

}