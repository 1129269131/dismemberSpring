package com.koala.day01beandefinition.springComponents.springContext.context.expression;

import com.koala.day01beandefinition.springComponents.springBeans.factory.config.BeanExpressionContext;
import com.koala.day01beandefinition.springComponents.springBeans.factory.config.BeanExpressionResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanExpressionException;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by koala on 2021-08-29
 */
public class StandardBeanExpressionResolver implements BeanExpressionResolver {

    /** Default expression prefix: "#{". */
    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";

    /** Default expression suffix: "}". */
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";


    private String expressionPrefix = DEFAULT_EXPRESSION_PREFIX;

    private String expressionSuffix = DEFAULT_EXPRESSION_SUFFIX;

    private ExpressionParser expressionParser;

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>(256);

    private final Map<BeanExpressionContext, StandardEvaluationContext> evaluationCache = new ConcurrentHashMap<>(8);

    private final ParserContext beanExpressionParserContext = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }
        @Override
        public String getExpressionPrefix() {
            return expressionPrefix;
        }
        @Override
        public String getExpressionSuffix() {
            return expressionSuffix;
        }
    };


    /**
     * Create a new {@code StandardBeanExpressionResolver} with default settings.
     */
    public StandardBeanExpressionResolver() {
        this.expressionParser = new SpelExpressionParser();
    }

    /**
     * Create a new {@code StandardBeanExpressionResolver} with the given bean class loader,
     * using it as the basis for expression compilation.
     * @param beanClassLoader the factory's bean class loader
     */
    public StandardBeanExpressionResolver(@Nullable ClassLoader beanClassLoader) {
        this.expressionParser = new SpelExpressionParser(new SpelParserConfiguration(null, beanClassLoader));
    }


    /**
     * Set the prefix that an expression string starts with.
     * The default is "#{".
     * @see #DEFAULT_EXPRESSION_PREFIX
     */
    public void setExpressionPrefix(String expressionPrefix) {
        Assert.hasText(expressionPrefix, "Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }

    /**
     * Set the suffix that an expression string ends with.
     * The default is "}".
     * @see #DEFAULT_EXPRESSION_SUFFIX
     */
    public void setExpressionSuffix(String expressionSuffix) {
        Assert.hasText(expressionSuffix, "Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }

    /**
     * Specify the EL parser to use for expression parsing.
     * <p>Default is a {@link SpelExpressionParser},
     * compatible with standard Unified EL style expression syntax.
     */
    public void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }


    @Override
    @Nullable
    public Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException {
        if (!StringUtils.hasLength(value)) {
            return value;
        }
        try {
            Expression expr = this.expressionCache.get(value);
            if (expr == null) {
                expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
                this.expressionCache.put(value, expr);
            }
            StandardEvaluationContext sec = this.evaluationCache.get(evalContext);
            if (sec == null) {
                sec = new StandardEvaluationContext(evalContext);
                sec.addPropertyAccessor(new BeanExpressionContextAccessor());
                sec.addPropertyAccessor(new BeanFactoryAccessor());
                sec.addPropertyAccessor(new MapAccessor());
                sec.addPropertyAccessor(new EnvironmentAccessor());
                sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
                sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
                ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
                if (conversionService != null) {
                    sec.setTypeConverter(new StandardTypeConverter(conversionService));
                }
                customizeEvaluationContext(sec);
                this.evaluationCache.put(evalContext, sec);
            }
            return expr.getValue(sec);
        }
        catch (Throwable ex) {
            throw new BeanExpressionException("Expression parsing failed", ex);
        }
    }

    /**
     * Template method for customizing the expression evaluation context.
     * <p>The default implementation is empty.
     */
    protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
    }

}