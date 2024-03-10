package com.tgf.advanced.validator.reflectionutil;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Util {
    private static List<Class> supportedAnnotations = List.of(RequestMapping.class, PutMapping.class, GetMapping.class, PostMapping.class, PatchMapping.class, DeleteMapping.class);

    private Util() {
    }

    public static Class<?> findClazzContainsRestController(Map<String, Object> instances, String path, List<String> parameters) {
        for (Object instance : instances.values()) {
            Method method = findMethodWithPathAndParameters(AopProxyUtils.ultimateTargetClass(instance), path, parameters);
            if (method != null) {
                return AopProxyUtils.ultimateTargetClass(instance);
            }
        }
        return null;
    }

    public static Method findMethodWithPathAndParameters(Class<?> clazz, String path, List<String> parameters) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(Util::isMethodContainsMapping)
                .filter(method -> containsPath(getAnnotationValue(method), path, clazz) &&
                        containsParameters(method, parameters))
                .findFirst().orElse(null);
    }

    private static String[] getAnnotationValue(Method method) {
        for (Class supportedAnnotation : supportedAnnotations) {
            Annotation annotation = method.getAnnotation(supportedAnnotation);
            if (annotation != null) {
                return switch (supportedAnnotation.getSimpleName()) {
                    case "PutMapping" -> ((PutMapping) annotation).value();
                    case "GetMapping" -> ((GetMapping) annotation).value();
                    case "PostMapping" -> ((PostMapping) annotation).value();
                    case "PatchMapping" -> ((PatchMapping) annotation).value();
                    case "DeleteMapping" -> ((DeleteMapping) annotation).value();
                    default -> ((RequestMapping) annotation).value();
                };
            }
        }
        return null;
    }

    private static boolean containsPath(String[] paths, String path, Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return Arrays.stream(requestMapping.value())
                    .anyMatch(s -> Arrays.stream(paths).anyMatch(s1 -> {
                        return path.contains(s + s1);
                    }));
        }

        return Arrays.asList(paths).contains(path);
    }

    public static boolean isMethodContainsMapping(Method method) {
        return supportedAnnotations.stream().anyMatch(aClass -> method.getAnnotation(aClass) != null);
    }

    public static boolean containsParameters(Method method, List<String> parameters) {
        return Arrays.stream(method.getParameters()).allMatch(parameter -> parameters.contains(parameter.getName()));
    }

    public static Object findFieldInObject(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (fieldName.contains(".")) {
            String[] fieldNames = fieldName.split("\\.");
            for (int i = 1; target != null && i < fieldNames.length; i++) {
                String name = fieldNames[i];
                Field f = target.getClass().getDeclaredField(name);
                f.setAccessible(true);
                target = f.get(target);
                f.setAccessible(false);
            }
        }
        return target;
    }
}
