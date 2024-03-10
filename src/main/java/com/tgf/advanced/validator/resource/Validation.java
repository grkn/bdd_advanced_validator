package com.tgf.advanced.validator.resource;

import com.tgf.advanced.validator.condition.Condition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Validation {

    private String path;
    private String method;
    private Class<?> clazz;
    private List<Parameter> parameters;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Parameter {
        private String fieldName;
        private Condition condition;
        private Class<?> fieldType;
    }
}
