package com.tgf.advanced.validator.config;

import com.tgf.advanced.validator.aspect.ValidationAspect;
import com.tgf.advanced.validator.condition.ConditionContext;
import com.tgf.advanced.validator.core.ValidationGenerator;
import com.tgf.advanced.validator.resource.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.IOException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class ValidationConfig {

    private final ValidationGenerator validationGenerator;
    private final ConditionContext conditionContext;

    @Value("${feature.path:validation.feature}")
    private String featurePath;

    @Value("${regex.path:validation.regex}")
    private String regexPath;

    @Bean
    @Qualifier("readableValidations")
    public Map<String, Validation> validations() throws IOException {
        return validationGenerator.createValidationList(featurePath, regexPath);
    }

    @Bean
    public ValidationAspect createAspect(@Qualifier("readableValidations") Map<String, Validation> classValidationMap) {
        return new ValidationAspect(classValidationMap, conditionContext);
    }

}
