package com.tgf.advanced.validator.aspect;

import com.tgf.advanced.validator.condition.ConditionContext;
import com.tgf.advanced.validator.reflectionutil.Util;
import com.tgf.advanced.validator.resource.Validation;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import java.util.Map;

@RequiredArgsConstructor
@Aspect
public class ValidationAspect {

    private final Map<String, Validation> validations;
    private final ConditionContext conditionContext;

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void post() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void put() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void delete() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void patch() {
    }

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void request() {
    }


    @Pointcut(value = "getMapping() || post() || put() || delete() || patch() || request()")
    public void methods() {
    }

    @Around(value = "methods()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        Class clazz = joinPoint.getSignature().getDeclaringType();
        try {
            Validation validation = validations.get(clazz.getName());
            if (validation != null) {
                validation.getParameters().forEach(parameter -> {
                    try {
                        for (int i = 0; i < joinPoint.getArgs().length; i++) {
                            Object arg = joinPoint.getArgs()[i];
                            if (codeSignature.getParameterNames()[i].equals(parameter.getFieldName())) {
                                validateParameters(parameter, arg);
                            }
                        }

                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(String.format("Check your field and object that you want to validate has field name %s", parameter.getFieldName()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable e) {
            throw e;
        }
    }

    private void validateParameters(Validation.Parameter parameter, Object arg) throws NoSuchFieldException, IllegalAccessException {
        Object value = arg == null ? null : Util.findFieldInObject(arg, parameter.getCondition().getFieldName());
        conditionContext.selectCondition(parameter.getCondition().getType())
                .ifPresent(condition -> {
                    condition.setFieldType(value == null ? null : value.getClass());
                    condition.validateByCondition(() -> value);
                });
    }
}
