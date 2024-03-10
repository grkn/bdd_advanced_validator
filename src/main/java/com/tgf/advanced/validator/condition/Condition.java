package com.tgf.advanced.validator.condition;

import java.util.function.Supplier;

public interface Condition<T> {
    void setFieldName(String name);

    String getFieldName();

    void setFieldType(Class<?> type);

    Class<?> getFieldType();

    boolean findByType(String type);

    String getType();

    boolean validateByCondition(Supplier<T> supplier) throws RuntimeException;
}
