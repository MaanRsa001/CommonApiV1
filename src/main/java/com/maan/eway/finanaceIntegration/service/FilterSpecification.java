package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.req.FilterCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

public class FilterSpecification<T> implements Specification<T> {

    private FilterCriteria criteria;

    public FilterSpecification(FilterCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        String operation = criteria.getOperation();
        String key = criteria.getKey();
        Object value = criteria.getValue();

        switch (operation) {
            case ":": // EQUAL
                return builder.equal(root.get(key), value);

            case "LIKE": // LIKE (case-insensitive)
                return builder.like(
                        builder.lower(root.get(key).as(String.class)),
                        value.toString().toLowerCase()
                );

            case "IN": // IN clause
                String[] values = value.toString().split(",");
                List<String> valueList = Arrays.asList(values);
                return root.get(key).in(valueList);

            case "@": // CONTAINS (for strings)
                return builder.like(
                        builder.lower(root.get(key).as(String.class)),
                        "%" + value.toString().toLowerCase() + "%"
                );

            case ">": // GREATER THAN
                return builder.greaterThan(root.get(key), value.toString());

            case "<": // LESS THAN
                return builder.lessThan(root.get(key), value.toString());

            case ">=": // GREATER THAN OR EQUAL
                return builder.greaterThanOrEqualTo(root.get(key), value.toString());

            case "<=": // LESS THAN OR EQUAL
                return builder.lessThanOrEqualTo(root.get(key), value.toString());

            default:
                return null;
        }
    }
}