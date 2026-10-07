package com.maan.eway.viewAll.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonCWESetRes {
	
	@JsonProperty("Common Clauses Details")
	private List<Map<String, Object>> condition;
	
	@JsonProperty("Common Warranty Details")
	private List<Map<String, Object>> warranty;
	
	@JsonProperty("Common Exclusion Details")
	private List<Map<String, Object>> exclusionList;

}
