package com.koala.day03refresh.springComponents.springBeans.factory.config;

import com.koala.day03refresh.springComponents.springBeans.factory.config.BeanExpressionResolver;
import com.koala.day03refresh.springComponents.springBeans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * Create by koala on 2021-08-29
 */
public class EmbeddedValueResolver implements StringValueResolver {

    private final BeanExpressionContext exprContext;

    @Nullable
    private final BeanExpressionResolver exprResolver;


    public EmbeddedValueResolver(ConfigurableBeanFactory beanFactory) {
        this.exprContext = new BeanExpressionContext(beanFactory, null);
        this.exprResolver = beanFactory.getBeanExpressionResolver();
    }


    @Override
    @Nullable
    public String resolveStringValue(String strVal) {
        String value = this.exprContext.getBeanFactory().resolveEmbeddedValue(strVal);
        if (this.exprResolver != null && value != null) {
            Object evaluated = this.exprResolver.evaluate(value, this.exprContext);
            value = (evaluated != null ? evaluated.toString() : null);
        }
        return value;
    }

}
