# Behaivor Driven Constraint Validator.

## What is solved with this library

You don't need to check all classes, fields and constraints. It is in a file and you can easily find it in one place.

You can create your own standardization and simply validate your data transfer object or input fields.

It is extendable to increase your validators and it is free to use and understand.

Of course there are several reflections going on in the library but it is just for determining classes, methods and fields.

Most crucial part is when you need to know about an api that is not implemented by you, then you need to know what is mandatory or not. 
If you write good explanation to your validation then it will be solved directly.

## How to use it

I did not put library to maven repository so I will explain how to use it.

1- clone repository
2- run maven clean install
3- add dependency to pom or gradle file.

```
      <dependency>
            <groupId>com.tgf</groupId>
            <artifactId>advanced.validator</artifactId>
            <version>1.0.0</version>
      </dependency>
```
4- Enable library with @EnableAdvancedValidation which can put on any spring class.

5- Write feature file

```
Scenario - Validation for example flow.
Given Method is Post and Endpoint is /tgf/{data}
When User sends @RequestParam as param
And User sends @RequestBody as sampleDto
And User sends @PathVariable as data
Then param can not be empty
And sampleDto.id can not be null
And data can not be blank
```

6- Write your regex file
```
(Given|And)( Method is )(\w*)( and Endpoint is )(.*)
(When|And) User sends @\w* as (.*)
(Then|And) ((\w|\.)*) can not be (.*)
(Then|And) ((\w|\.)*) must be validated by (.*)
```

7- According to your regex file you need to write three java class.

```
@Component
public class GivenRegexExtractor implements GivenRegexResultExtractor<Matcher, Map<MandatoryKeyword, String>> {
    @Override
    public Map<MandatoryKeyword, String> parse(Matcher matcher) {
        Map<MandatoryKeyword, String> map = new HashMap<>();
        map.put(MandatoryKeyword.METHOD,matcher.group(3)); // Given Method is Post and Endpoint is /tgf/{data}: Post
        map.put(MandatoryKeyword.PATH,matcher.group(5));  // Given Method is Post and Endpoint is /tgf/{data}: /tgf/{data}
        return map;
    }
}

@Component
public class ThenRegexExtractor implements ThenRegexResultExtractor<Matcher, CustomPair> {
    @Override
    public CustomPair parse(Matcher matcher) {
        CustomPair customPair = new CustomPair();
        customPair.setFieldName(matcher.group(2));  // field name in then statement
        customPair.setCondition(matcher.group(4)); // empty, blank or null
        return customPair;
    }
}


@Component
public class WhenRegexExtractor implements WhenRegexResultExtractor<Matcher, String> {
    @Override
    public String parse(Matcher matcher) {
        return matcher.group(2); // field name in when statement
    }
}
```

That's all actually. Your endpoint of POST /tgf/{data} is validated by advanced validator.
 * param variable can not be empty
 * sampleDto.id variable can not be null
 * data variable can not be blank

Above statement is validated and for each Mapping of (PUT,GET,POST,DELETE,PATCH) are supported.

## Advanced usage

Currently there are three validation class which can be extended in time. I need regex constraint validator.
Example in below code is Empty validator.
You can also extend BaseCondition abstract class and add new conditions.

```
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
```


You can find example documents in libraries resource file.


## Advantages

1- All validations can be read by any developer or tester.(Human readable)

2- You can simply customize it with your validators.

3- All validations are prepared on spring startup so you can not change it and it is immutable.

4- For each scenario it is easy to use and it is easy to understand which fields are validated.

## Disadvantages

1- According to your validation file boot start can be effected. (I dont think that you have 10000 endpoints in application)

2- Java specifications are not considered in development so there is not any standardization.

3- You need to know regex.

