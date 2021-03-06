package com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support;

import com.koala.day02defaultlistablebeanfactory.springComponents.springBeans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by koala on 2021-08-29
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

    /** Cache of singleton objects created by FactoryBeans: FactoryBean name to object. */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);


    /**
     * Determine the type for the given FactoryBean.
     * @param factoryBean the FactoryBean instance to check
     * @return the FactoryBean's object type,
     * or {@code null} if the type cannot be determined yet
     */
    @Nullable
    protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(
                        (PrivilegedAction<Class<?>>) factoryBean::getObjectType, getAccessControlContext());
            }
            else {
                return factoryBean.getObjectType();
            }
        }
        catch (Throwable ex) {
            // Thrown from the FactoryBean's getObjectType implementation.
            logger.info("FactoryBean threw exception from getObjectType, despite the contract saying " +
                    "that it should return null if the type of its object cannot be determined yet", ex);
            return null;
        }
    }

    /**
     * Obtain an object to expose from the given FactoryBean, if available
     * in cached form. Quick check for minimal synchronization.
     * @param beanName the name of the bean
     * @return the object obtained from the FactoryBean,
     * or {@code null} if not available
     */
    @Nullable
    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    /**
     * Obtain an object to expose from the given FactoryBean.
     * @param factory the FactoryBean instance
     * @param beanName the name of the bean
     * @param shouldPostProcess whether the bean is subject to post-processing
     * @return the object obtained from the FactoryBean
     * @throws BeanCreationException if FactoryBean object creation failed
     * @see FactoryBean#getObject()
     */
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory.isSingleton() && containsSingleton(beanName)) {
            synchronized (getSingletonMutex()) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = doGetObjectFromFactoryBean(factory, beanName);
                    // Only post-process and store if not put there already during getObject() call above
                    // (e.g. because of circular reference processing triggered by custom getBean calls)
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object = alreadyThere;
                    }
                    else {
                        if (shouldPostProcess) {
                            if (isSingletonCurrentlyInCreation(beanName)) {
                                // Temporarily return non-post-processed object, not storing it yet..
                                return object;
                            }
                            beforeSingletonCreation(beanName);
                            try {
                                object = postProcessObjectFromFactoryBean(object, beanName);
                            }
                            catch (Throwable ex) {
                                throw new BeanCreationException(beanName,
                                        "Post-processing of FactoryBean's singleton object failed", ex);
                            }
                            finally {
                                afterSingletonCreation(beanName);
                            }
                        }
                        if (containsSingleton(beanName)) {
                            this.factoryBeanObjectCache.put(beanName, object);
                        }
                    }
                }
                return object;
            }
        }
        else {
            Object object = doGetObjectFromFactoryBean(factory, beanName);
            if (shouldPostProcess) {
                try {
                    object = postProcessObjectFromFactoryBean(object, beanName);
                }
                catch (Throwable ex) {
                    throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
                }
            }
            return object;
        }
    }

    /**
     * Obtain an object to expose from the given FactoryBean.
     * @param factory the FactoryBean instance
     * @param beanName the name of the bean
     * @return the object obtained from the FactoryBean
     * @throws BeanCreationException if FactoryBean object creation failed
     * @see FactoryBean#getObject()
     */
    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
        Object object;
        try {
            if (System.getSecurityManager() != null) {
                AccessControlContext acc = getAccessControlContext();
                try {
                    object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
                }
                catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
            else {
                object = factory.getObject();
            }
        }
        catch (FactoryBeanNotInitializedException ex) {
            throw new BeanCurrentlyInCreationException(beanName, ex.toString());
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
        }

        // Do not accept a null value for a FactoryBean that's not fully
        // initialized yet: Many FactoryBeans just return null then.
        if (object == null) {
            if (isSingletonCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(
                        beanName, "FactoryBean which is currently in creation returned null from getObject");
            }
            object = new NullBean();
        }
        return object;
    }

    /**
     * Post-process the given object that has been obtained from the FactoryBean.
     * The resulting object will get exposed for bean references.
     * <p>The default implementation simply returns the given object as-is.
     * Subclasses may override this, for example, to apply post-processors.
     * @param object the object obtained from the FactoryBean.
     * @param beanName the name of the bean
     * @return the object to expose
     * @throws BeansException if any post-processing failed
     */
    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    /**
     * Get a FactoryBean for the given bean if possible.
     * @param beanName the name of the bean
     * @param beanInstance the corresponding bean instance
     * @return the bean instance as FactoryBean
     * @throws BeansException if the given bean cannot be exposed as a FactoryBean
     */
    protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanCreationException(beanName,
                    "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        }
        return (FactoryBean<?>) beanInstance;
    }

    /**
     * Overridden to clear the FactoryBean object cache as well.
     */
    @Override
    protected void removeSingleton(String beanName) {
        synchronized (getSingletonMutex()) {
            super.removeSingleton(beanName);
            this.factoryBeanObjectCache.remove(beanName);
        }
    }

    /**
     * Overridden to clear the FactoryBean object cache as well.
     */
    @Override
    protected void clearSingletonCache() {
        synchronized (getSingletonMutex()) {
            super.clearSingletonCache();
            this.factoryBeanObjectCache.clear();
        }
    }

    /**
     * Return the security context for this bean factory. If a security manager
     * is set, interaction with the user code will be executed using the privileged
     * of the security context returned by this method.
     * @see AccessController#getContext()
     */
    protected AccessControlContext getAccessControlContext() {
        return AccessController.getContext();
    }

}
