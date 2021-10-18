package com.koala.day06aop.springComponents.springContext.context.expression;

import com.koala.day06aop.springComponents.springBeans.factory.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

/**
 * Create by koala on 2021-08-29
 */
public class BeanFactoryResolver implements BeanResolver {

    private final BeanFactory beanFactory;


    /**
     * Create a new {@link org.springframework.context.expression.BeanFactoryResolver} for the given factory.
     * @param beanFactory the {@link BeanFactory} to resolve bean names against
     */
    public BeanFactoryResolver(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }


    @Override
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        try {
            return this.beanFactory.getBean(beanName);
        }
        catch (BeansException ex) {
            throw new AccessException("Could not resolve bean reference against BeanFactory", ex);
        }
    }

}
