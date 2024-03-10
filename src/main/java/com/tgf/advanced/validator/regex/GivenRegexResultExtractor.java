package com.tgf.advanced.validator.regex;

import com.tgf.advanced.validator.constant.MandatoryKeyword;

import java.util.Map;
import java.util.regex.Matcher;

@FunctionalInterface
public interface GivenRegexResultExtractor<T extends Matcher, R extends Map<MandatoryKeyword, String>> {

    R parse(T t);
}
