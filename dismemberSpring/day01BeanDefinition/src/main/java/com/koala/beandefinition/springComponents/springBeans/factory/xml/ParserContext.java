package com.koala.beandefinition.springComponents.springBeans.factory.xml;

import com.koala.beandefinition.springComponents.springBeans.factory.config.BeanDefinition;
import com.koala.beandefinition.springComponents.springBeans.factory.parsing.BeanComponentDefinition;
import com.koala.beandefinition.springComponents.springBeans.factory.parsing.ComponentDefinition;
import com.koala.beandefinition.springComponents.springBeans.factory.parsing.CompositeComponentDefinition;
import com.koala.beandefinition.springComponents.springBeans.factory.support.BeanDefinitionReaderUtils;
import com.koala.beandefinition.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Create by koala on 2021-08-28
 */
public final class ParserContext {

    private final XmlReaderContext readerContext;

    private final BeanDefinitionParserDelegate delegate;

    @Nullable
    private BeanDefinition containingBeanDefinition;

    private final Deque<CompositeComponentDefinition> containingComponents = new ArrayDeque<>();


    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate) {
        this.readerContext = readerContext;
        this.delegate = delegate;
    }

    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate,
                         @Nullable BeanDefinition containingBeanDefinition) {

        this.readerContext = readerContext;
        this.delegate = delegate;
        this.containingBeanDefinition = containingBeanDefinition;
    }


    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.readerContext.getRegistry();
    }

    public final BeanDefinitionParserDelegate getDelegate() {
        return this.delegate;
    }

    @Nullable
    public final BeanDefinition getContainingBeanDefinition() {
        return this.containingBeanDefinition;
    }

    public final boolean isNested() {
        return (this.containingBeanDefinition != null);
    }

    public boolean isDefaultLazyInit() {
        return BeanDefinitionParserDelegate.TRUE_VALUE.equals(this.delegate.getDefaults().getLazyInit());
    }

    @Nullable
    public Object extractSource(Object sourceCandidate) {
        return this.readerContext.extractSource(sourceCandidate);
    }

    @Nullable
    public CompositeComponentDefinition getContainingComponent() {
        return this.containingComponents.peek();
    }

    public void pushContainingComponent(CompositeComponentDefinition containingComponent) {
        this.containingComponents.push(containingComponent);
    }

    public CompositeComponentDefinition popContainingComponent() {
        return this.containingComponents.pop();
    }

    public void popAndRegisterContainingComponent() {
        registerComponent(popContainingComponent());
    }

    public void registerComponent(ComponentDefinition component) {
        CompositeComponentDefinition containingComponent = getContainingComponent();
        if (containingComponent != null) {
            containingComponent.addNestedComponent(component);
        }
        else {
            this.readerContext.fireComponentRegistered(component);
        }
    }

    public void registerBeanComponent(BeanComponentDefinition component) {
        BeanDefinitionReaderUtils.registerBeanDefinition(component, getRegistry());
        registerComponent(component);
    }

}
