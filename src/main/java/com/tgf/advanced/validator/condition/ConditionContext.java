package com.tgf.advanced.validator.condition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConditionContext {

    private final List<Condition> conditions;

    public final Optional<Condition> selectCondition(String name) {
        for (Condition condition : conditions) {
            if (condition.findByType(name)) {
                return Optional.of(condition);
            }
        }
        return Optional.empty();
    }

}
