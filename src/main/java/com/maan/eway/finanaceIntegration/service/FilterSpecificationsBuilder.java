package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.req.FilterCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterSpecificationsBuilder<T> {

    private final List<FilterCriteria> params;

    public FilterSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public FilterSpecificationsBuilder<T> with(String key, String operation, Object value) {
        params.add(new FilterCriteria(key, operation, value));
        return this;
    }

    public FilterSpecificationsBuilder<T> with(Object searchObject) {

        if (searchObject == null) {
            return this;
        }

        Field[] fields = searchObject.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(searchObject);

                if (value == null) continue;

                String fieldName = field.getName();
                String operation;

                // String → LIKE (case-insensitive) with wildcards
                if (value instanceof String) {
                    String strValue = (String) value;
                    if (!strValue.trim().isEmpty()) {
                        // Check if it contains comma (IN operation)
                        if (strValue.contains(",")) {
                            params.add(new FilterCriteria(fieldName, "IN", strValue));
                        }
                        // Check if it explicitly contains % (user wants LIKE)
                        else if (strValue.contains("%")) {
                            params.add(new FilterCriteria(fieldName, "LIKE", strValue.toLowerCase()));
                        }
                        // Default for strings: contains (partial match)
                        else {
                            params.add(new FilterCriteria(fieldName, "@", strValue));
                        }
                    }
                }
                // Numbers → EQUAL
                else if (value instanceof Long || value instanceof Integer ||
                        value instanceof Double || value instanceof Float) {
                    params.add(new FilterCriteria(fieldName, ":", value));
                }
                // Boolean → EQUAL
                else if (value instanceof Boolean) {
                    params.add(new FilterCriteria(fieldName, ":", value));
                }
                // Date → EQUAL
                else if (value instanceof Date) {
                    params.add(new FilterCriteria(fieldName, ":", value));
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public Specification<T> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification<T>> specs = new ArrayList<>();
        for (FilterCriteria param : params) {
            specs.add(new FilterSpecification<>(param));
        }

        Specification<T> result = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }

        return result;
    }
}