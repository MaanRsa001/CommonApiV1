package com.maan.eway.upgrade.criteria;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SearchCriteria {
     
	private String key;	
    private String operation;     
    private Object value;
    private List<String> values;
    private String key2;
}
