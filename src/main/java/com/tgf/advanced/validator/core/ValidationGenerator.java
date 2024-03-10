package com.tgf.advanced.validator.core;

import com.tgf.advanced.validator.condition.Condition;
import com.tgf.advanced.validator.condition.ConditionContext;
import com.tgf.advanced.validator.constant.Keyword;
import com.tgf.advanced.validator.constant.MandatoryKeyword;
import com.tgf.advanced.validator.reflectionutil.Util;
import com.tgf.advanced.validator.regex.GivenRegexResultExtractor;
import com.tgf.advanced.validator.regex.ThenRegexResultExtractor;
import com.tgf.advanced.validator.regex.WhenRegexResultExtractor;
import com.tgf.advanced.validator.resource.CustomPair;
import com.tgf.advanced.validator.resource.Validation;
import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ValidationGenerator {
    private final ApplicationContext applicationContext;
    private final GivenRegexResultExtractor<Matcher, Map<MandatoryKeyword, String>> givenRegexResultExtractor;
    private final WhenRegexResultExtractor<Matcher, String> whenRegexResultExtractor;
    private final ThenRegexResultExtractor<Matcher, CustomPair> thenRegexResultExtractor;
    private final ConditionContext conditionContext;

    public Map<String, Validation> createValidationList(String featurePath, String regexPath) throws IOException {
        List<Envelope> envelopes = getEnvelopes(featurePath);
        List<Pattern> patterns = getPatterns(regexPath);

        Assert.notNull(envelopes, "Cucumber file does not exist");
        Assert.notEmpty(envelopes, "Cucumber file is empty");
        Assert.isTrue(envelopes.get(0).getSource().isPresent(), "Cucumber file is empty");
        Assert.notEmpty(patterns, "Regex Patterns are empty");


        Map<String, Validation> scenarioValidationMap = new HashMap<>();

        for (int i = 1; i < envelopes.size(); i++) {
            String[] sentences = envelopes.get(0).getSource().get().getData().split(System.lineSeparator());
            String scenario = sentences[0];
            Validation validation = new Validation();

            validation.setParameters(new ArrayList<>());



            for (int k = 1; k < sentences.length; k++) {
                String s = sentences[k];

                for (int j = 0; j < patterns.size(); j++) {

                    Pattern pattern = patterns.get(j);
                    Matcher matcher = pattern.matcher(s);

                    if (matcher.matches() && pattern.pattern().toUpperCase(Locale.ROOT).contains(Keyword.GIVEN.name())) {
                        Map<MandatoryKeyword, String> map = givenRegexResultExtractor.parse(matcher);
                        validation.setMethod(map.get(MandatoryKeyword.METHOD));
                        validation.setPath(map.get(MandatoryKeyword.PATH));
                        break;
                    } else if (matcher.matches() && pattern.pattern().toUpperCase(Locale.ROOT).contains(Keyword.WHEN.name())) {
                        String field = whenRegexResultExtractor.parse(matcher);
                        Validation.Parameter fieldParameter = new Validation.Parameter();
                        fieldParameter.setFieldName(field);
                        validation.getParameters().add(fieldParameter);
                        break;
                    } else if (matcher.matches() && pattern.pattern().toUpperCase(Locale.ROOT).contains(Keyword.THEN.name())) {
                        CustomPair pair = thenRegexResultExtractor.parse(matcher);
                        String conditionField = pair.getCondition();
                        String fieldToBeChecked = pair.getFieldName().contains(".") ? pair.getFieldName().split("\\.")[0] : pair.getFieldName();
                        Validation.Parameter fieldParameter = validation
                                .getParameters()
                                .stream()
                                .filter(objectParameter -> objectParameter.getFieldName().equals(fieldToBeChecked))
                                .findFirst().orElse(null);
                        Assert.notNull(fieldParameter, "Feature file can not map field with field to be check." +
                                "When statements field name and Then statements field name must be equal");

                        Condition condition = conditionContext.selectCondition(conditionField).orElse(null);
                        Assert.notNull(condition, String.format("Condition is not supported. Condition type is %s", conditionField));
                        condition.setFieldName(pair.getFieldName());

                        fieldParameter.setCondition(condition);
                        break;
                    }

                }

            }

            Class clazz = Util.findClazzContainsRestController(applicationContext.getBeansWithAnnotation(RestController.class), validation.getPath(),
                    validation.getParameters().stream().map(Validation.Parameter::getFieldName).collect(Collectors.toList()));
            validation.setClazz(clazz);
            scenarioValidationMap.put(clazz.getName(), validation);
        }
        return scenarioValidationMap;
    }

    private static List<Envelope> getEnvelopes(String featurePath) throws IOException {
        GherkinParser gherkinParser = GherkinParser.builder().idGenerator(() -> UUID.randomUUID().toString()).includeSource(true).build();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(featurePath);
        List<Envelope> envelopes = gherkinParser.parse("", inputStream).toList();
        return envelopes;
    }

    private static List<Pattern> getPatterns(String featurePath) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(featurePath), byteArrayOutputStream);
        String[] regexList = byteArrayOutputStream.toString().split(System.lineSeparator());
        List<Pattern> patterns = Arrays.stream(regexList).map(Pattern::compile).collect(Collectors.toList());
        return patterns;
    }
}
