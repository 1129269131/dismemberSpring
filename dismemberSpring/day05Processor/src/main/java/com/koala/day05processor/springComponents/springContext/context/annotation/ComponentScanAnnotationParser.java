package com.koala.day05processor.springComponents.springContext.context.annotation;

import com.koala.day05processor.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day05processor.springComponents.springBeans.factory.support.BeanDefinitionRegistry;
import com.koala.day05processor.springComponents.springBeans.factory.support.BeanNameGenerator;
import com.koala.day05processor.springComponents.springCore.core.annotation.AnnotationAttributes;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Create by koala on 2021-09-05
 */
class ComponentScanAnnotationParser {

    private final Environment environment;

    private final ResourceLoader resourceLoader;

    private final BeanNameGenerator beanNameGenerator;

    private final BeanDefinitionRegistry registry;


    public ComponentScanAnnotationParser(Environment environment, ResourceLoader resourceLoader,
                                         BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry) {

        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.beanNameGenerator = beanNameGenerator;
        this.registry = registry;
    }


    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry,
                componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);

        Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
        boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass || generatorClass.getName() == "org.springframework.beans.factory.support.BeanNameGenerator");//20210905修改 || 及之后的，不知道为什么
        scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator :
                BeanUtils.instantiateClass(generatorClass));

        ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
        if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
            scanner.setScopedProxyMode(scopedProxyMode);
        }
        else {
            Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
            //scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));//20210905注释
        }

        scanner.setResourcePattern(componentScan.getString("resourcePattern"));

        /*for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter)) {
                scanner.addIncludeFilter(typeFilter);
            }
        }*/
        /*for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter)) {
                scanner.addExcludeFilter(typeFilter);
            }
        }*/

        boolean lazyInit = componentScan.getBoolean("lazyInit");
        if (lazyInit) {
            scanner.getBeanDefinitionDefaults().setLazyInit(true);
        }

        Set<String> basePackages = new LinkedHashSet<>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            Collections.addAll(basePackages, tokenized);
        }
        for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }

        scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
            @Override
            protected boolean matchClassName(String className) {
                return declaringClass.equals(className);
            }
        });
        return scanner.doScan(StringUtils.toStringArray(basePackages)); //day21：扫描器进行扫描
    }

    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = filterAttributes.getEnum("type");

        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                            "@ComponentScan ANNOTATION type filter requires an annotation type");
                    @SuppressWarnings("unchecked")
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass,
                            "@ComponentScan CUSTOM type filter requires a TypeFilter implementation");

                    TypeFilter filter = ParserStrategyUtils.instantiateClass(filterClass, TypeFilter.class,
                            this.environment, this.resourceLoader, this.registry);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
            }
        }

        for (String expression : filterAttributes.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ:
                    typeFilters.add(new AspectJTypeFilter(expression, this.resourceLoader.getClassLoader()));
                    break;
                case REGEX:
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with String pattern: " + filterType);
            }
        }

        return typeFilters;
    }

}