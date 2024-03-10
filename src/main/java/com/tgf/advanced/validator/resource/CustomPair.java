package com.tgf.advanced.validator.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CustomPair {
    private String fieldName;
    private String condition;
}