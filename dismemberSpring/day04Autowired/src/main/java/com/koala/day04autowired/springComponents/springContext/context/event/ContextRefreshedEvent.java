package com.koala.day04autowired.springComponents.springContext.context.event;

import com.koala.day04autowired.springComponents.springContext.context.ApplicationContext;
import com.koala.day04autowired.springComponents.springContext.context.event.ApplicationContextEvent;

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
