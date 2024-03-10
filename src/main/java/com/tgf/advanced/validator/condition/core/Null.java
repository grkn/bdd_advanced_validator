package com.tgf.advanced.validator.condition.core;

import com.tgf.advanced.validator.constant.Keyword;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Supplier;
@Component
public class Null extends BaseCondition<Object> {
    private static final String NULL = Keyword.NULL.name().toLowerCase(Locale.ROOT);

    public Null() {
        super(NULL);
    }

    @Override
    public boolean findByType(String type) {
        return NULL.equals(type);
    }

    @Override
    public boolean validateByCondition(Supplier<Object> supplier) throws RuntimeException {
        if (supplier.get() == null) {
            // can be custom exception if it given in classloader
            throw new RuntimeException(String.format("NULL value is not permitted for field %s", getFieldName()));
        }
        return true;
    }
}