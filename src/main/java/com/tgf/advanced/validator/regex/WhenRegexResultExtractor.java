package com.tgf.advanced.validator.regex;

import java.util.Map;
import java.util.regex.Matcher;
@FunctionalInterface
public interface WhenRegexResultExtractor<T extends Matcher, R extends String> {
    R parse(T t);
}
