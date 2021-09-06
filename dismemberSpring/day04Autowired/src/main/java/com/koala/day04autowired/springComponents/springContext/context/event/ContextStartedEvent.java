package com.koala.day04autowired.springComponents.springContext.context.event;

import com.koala.day04autowired.springComponents.springContext.context.ApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.event.ApplicationContextEvent;

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
