package com.tgf.advanced.validator.condition.core;

import com.tgf.advanced.validator.condition.Condition;

import java.util.function.Supplier;

public abstract class BaseCondition<T> implements Condition<T> {

    private String fieldName;
    private Class<?> fieldType;

    private String type;

    public BaseCondition(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setFieldName(String name) {
        fieldName = name;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public abstract boolean findByType(String type);

    public abstract boolean validateByCondition(Supplier<T> supplier) throws RuntimeException;
}
