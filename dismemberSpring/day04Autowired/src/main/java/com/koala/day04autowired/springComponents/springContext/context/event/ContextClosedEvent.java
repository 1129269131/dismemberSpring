package com.koala.day04autowired.springComponents.springContext.context.event;

import com.koala.day04autowired.springComponents.springContext.context.ApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.event.ApplicationContextEvent;

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