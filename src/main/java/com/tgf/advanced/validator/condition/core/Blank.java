package com.tgf.advanced.validator.condition.core;

import com.tgf.advanced.validator.constant.Keyword;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Supplier;

@Component
public class Blank extends BaseCondition<String> {
    private static final String BLANK = Keyword.BLANK.name().toLowerCase(Locale.ROOT);

    public Blank() {
        super(BLANK);
    }

    @Override
    public boolean findByType(String type) {
        return BLANK.equals(type);
    }

    @Override
    public boolean validateByCondition(Supplier<String> supplier) throws RuntimeException {
        if(supplier.get() == null) {
            throw new RuntimeException(String.format("BLANK value is not permitted for field %s", getFieldName()));
        }
        if (supplier.get().isBlank()) {
            // can be custom exception if it given in classloader
            throw new RuntimeException(String.format("BLANK value is not permitted for field %s", getFieldName()));
        }
        return true;
    }
}