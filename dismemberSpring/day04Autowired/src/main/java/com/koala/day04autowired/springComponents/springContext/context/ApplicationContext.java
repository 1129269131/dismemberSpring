package com.koala.day04autowired.springComponents.springContext.context;

import com.koala.day04autowired.springComponents.springBeans.factory.HierarchicalBeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.ListableBeanFactory;
import com.koala.day04autowired.springComponents.springBeans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-08-29
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
        MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

    /**
     * Return the unique id of this application context.
     * @return the unique id of the context, or {@code null} if none
     */
    @Nullable
    String getId();

    /**
     * Return a name for the deployed application that this context belongs to.
     * @return a name for the deployed application, or the empty String by default
     */
    String getApplicationName();

    /**
     * Return a friendly name for this context.
     * @return a display name for this context (never {@code null})
     */
    String getDisplayName();

    /**
     * Return the timestamp when this context was first loaded.
     * @return the timestamp (ms) when this context was first loaded
     */
    long getStartupDate();

    /**
     * Return the parent context, or {@code null} if there is no parent
     * and this is the root of the context hierarchy.
     * @return the parent context, or {@code null} if there is no parent
     */
    @Nullable
    ApplicationContext getParent();

    /**
     * Expose AutowireCapableBeanFactory functionality for this context.
     * <p>This is not typically used by application code, except for the purpose of
     * initializing bean instances that live outside of the application context,
     * applying the Spring bean lifecycle (fully or partly) to them.
     * <p>Alternatively, the internal BeanFactory exposed by the
     * {@link ConfigurableApplicationContext} interface offers access to the
     * {@link AutowireCapableBeanFactory} interface too. The present method mainly
     * serves as a convenient, specific facility on the ApplicationContext interface.
     * <p><b>NOTE: As of 4.2, this method will consistently throw IllegalStateException
     * after the application context has been closed.</b> In current Spring Framework
     * versions, only refreshable application contexts behave that way; as of 4.2,
     * all application context implementations will be required to comply.
     * @return the AutowireCapableBeanFactory for this context
     * @throws IllegalStateException if the context does not support the
     * {@link AutowireCapableBeanFactory} interface, or does not hold an
     * autowire-capable bean factory yet (e.g. if {@code refresh()} has
     * never been called), or if the context has been closed already
     * @see ConfigurableApplicationContext#refresh()
     * @see ConfigurableApplicationContext#getBeanFactory()
     */
    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
