package com.koala.day04autowired.springComponents.springContext.context.annotation;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.MethodMetadata;

/**
 * Create by koala on 2021-09-05
 */
final class BeanMethod extends ConfigurationMethod {

    public BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        super(metadata, configurationClass);
    }

    @Override
    public void validate(ProblemReporter problemReporter) {
        if (getMetadata().isStatic()) {
            // static @Bean methods have no constraints to validate -> return immediately
            return;
        }

        if (this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName())) {
            if (!getMetadata().isOverridable()) {
                // instance @Bean methods within @Configuration classes must be overridable to accommodate CGLIB
                problemReporter.error(new BeanMethod.NonOverridableMethodError());
            }
        }
    }


    private class NonOverridableMethodError extends Problem {

        public NonOverridableMethodError() {
            super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue",
                    getMetadata().getMethodName()), getResourceLocation());
        }
    }
}

