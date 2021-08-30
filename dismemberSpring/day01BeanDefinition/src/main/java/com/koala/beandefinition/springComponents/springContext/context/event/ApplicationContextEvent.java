package com.koala.beandefinition.springComponents.springContext.context.event;

import com.koala.beandefinition.springComponents.springContext.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * Create by koala on 2021-08-29
 */
@SuppressWarnings("serial")
public abstract class ApplicationContextEvent extends ApplicationEvent {

    /**
     * Create a new ContextStartedEvent.
     * @param source the {@code ApplicationContext} that the event is raised for
     * (must not be {@code null})
     */
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    /**
     * Get the {@code ApplicationContext} that the event was raised for.
     */
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }

}

