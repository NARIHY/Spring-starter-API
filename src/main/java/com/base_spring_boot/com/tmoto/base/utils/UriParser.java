package com.base_spring_boot.com.tmoto.base.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UriParser {

    public static final String ALL_FIELDS = "all";

    private static final String QUERY_KEY_FIELD = "fields";
    private static final String QUERY_KEY_FIELD_ESCAPE = ":";

    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";

    public static final String SORT = "sort";
    public static final String NULL = "null";
    public static final String FROM_SEARCH = "fromSearch";

    private static final Set<String> NOT_QUERY_PARAMS = new HashSet<>();
    private static Pattern stringPattern = Pattern.compile("[A-Za-z]");

    static {
        NOT_QUERY_PARAMS.add(ALL_FIELDS);
        NOT_QUERY_PARAMS.add(QUERY_KEY_FIELD);
        NOT_QUERY_PARAMS.add(LIMIT);
        NOT_QUERY_PARAMS.add(OFFSET);
        NOT_QUERY_PARAMS.add(SORT);
        NOT_QUERY_PARAMS.add(NULL);
    }

    public static Set<String> getFieldsSelection(MultiValueMap<String, String> queryParameters) {
        Set<String> fieldSet = new HashSet<>();
        if (queryParameters != null) {
            // search for "all" parameter
            if (queryParameters.containsKey(ALL_FIELDS)) {
                fieldSet.add(ALL_FIELDS);
            }
            // search for "fields" parameters
            List<String> queryParameterField = queryParameters.get(QUERY_KEY_FIELD_ESCAPE + QUERY_KEY_FIELD);
            if (queryParameterField == null || queryParameterField.isEmpty()) {
                queryParameterField = queryParameters.get(QUERY_KEY_FIELD);
            }
            if (queryParameterField != null && !queryParameterField.isEmpty()) {
                queryParameterField.forEach(queryParameterValue -> {
                    String[] tokenArray = queryParameterValue.split(",");
                    fieldSet.addAll(Arrays.asList(tokenArray));
                });
            }
        }
        return fieldSet;
    }

    public static MultiValueMap<String, String> extractCriteria(MultiValueMap<String, String> queryParameters) {
        return extractCriteria(queryParameters, new ArrayList<>());
    }

    public static MultiValueMap<String, String> extractCriteria(MultiValueMap<String, String> queryParameters, List<String> filters) {
        MultiValueMap<String, String> criteria = new LinkedMultiValueMap<>();
        queryParameters.entrySet().stream().filter(entry -> !NOT_QUERY_PARAMS.contains(entry.getKey())).forEach(entry -> {
            List<String> values = entry.getValue();
            List<String> tempValues = new ArrayList<>();
            for (String value : values) {
                String[] tabValues = value.split(",");
                tempValues.addAll(Arrays.asList(tabValues));
            }
            if (filters.contains(entry.getKey())) {
                tempValues = tempValues.stream().map(value -> "%" + value + "%").collect(Collectors.toList());
            } else if (filters.contains("ALL") && tempValues.stream().allMatch(v -> isString(v) && !v.contains("%"))) {
                tempValues = tempValues.stream().map(value -> "%" + value + "%")
                        .collect(Collectors.toList());

            }
            criteria.put(entry.getKey(), tempValues);

        });
        if (queryParameters.containsKey(FROM_SEARCH)) criteria.set(FROM_SEARCH, "true");
        return criteria;
    }

    private static Boolean isString(String value) {
        final Matcher matcher = stringPattern.matcher(value);
        if (matcher.find()) {
            return !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false");
        }
        return false;
    }
}
