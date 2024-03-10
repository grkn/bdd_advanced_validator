package com.tgf.advanced.validator.regex;

import com.tgf.advanced.validator.resource.CustomPair;

import java.util.Map;
import java.util.regex.Matcher;

@FunctionalInterface
public interface ThenRegexResultExtractor<T extends Matcher, R extends CustomPair> {
    R parse(T t);
}