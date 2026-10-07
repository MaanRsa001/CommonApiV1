package com.maan.eway.upgrade.criteria;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
 

public class CustomSpecification implements Specification<Object> {

	private SearchCriteria criteria;
	
	public CustomSpecification(SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }else if(criteria.getOperation().equalsIgnoreCase("!")) {
        	return builder.notEqual(root.get(criteria.getKey()),criteria.getValue());        	
        	 
        }else if(criteria.getOperation().equalsIgnoreCase("{}")) {
        	builder.in(root.get(criteria.getKey()))
            .value( criteria.getValues());
        }
        return null;
	}

	 
	
	
	 
}
