package com.tgf.advanced.validator.condition.core;

import com.tgf.advanced.validator.constant.Keyword;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class Empty extends BaseCondition<Object> {
    private static final String EMPTY = Keyword.EMPTY.name().toLowerCase(Locale.ROOT);

    public Empty() {
        super(EMPTY);
    }

    @Override
    public boolean findByType(String type) {
        return EMPTY.equals(type);
    }

    @Override
    public boolean validateByCondition(Supplier<Object> supplier) throws RuntimeException {
        if(supplier.get() == null) {
            throw new RuntimeException(String.format("EMPTY value is not permitted for field %s", getFieldName()));
        }
        List<Type> interfaces = Arrays.stream(getFieldType().getGenericInterfaces()).toList();
        if (interfaces.contains(Collection.class)) {
            validateCollection(supplier);
        } else if (interfaces.contains(CharSequence.class)) {
            validateCharSequence(supplier);
        } else if (interfaces.contains(Map.class)) {
            validateMap(supplier);
        }
        return true;
    }

    private void validateCollection(Supplier<Object> supplier) {
        if (((Collection) supplier.get()).isEmpty()) {
            // can be custom exception if it given in classloader
            throw new RuntimeException(String.format("EMPTY value is not permitted for field %s", getFieldName()));
        }
    }

    private void validateCharSequence(Supplier<Object> supplier) {
        if (((CharSequence) supplier.get()).isEmpty()) {
            // can be custom exception if it given in classloader
            throw new RuntimeException(String.format("EMPTY value is not permitted for field %s", getFieldName()));
        }
    }

    private void validateMap(Supplier<Object> supplier) {
        if (((Map) supplier.get()).isEmpty()) {
            // can be custom exception if it given in classloader
            throw new RuntimeException(String.format("EMPTY value is not permitted for field %s", getFieldName()));
        }
    }
}
