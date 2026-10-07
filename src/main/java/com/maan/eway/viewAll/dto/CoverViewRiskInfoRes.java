package com.maan.eway.viewAll.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CoverViewRiskInfoRes {
	

	@JsonProperty("HEADER")
	KeyAndValueDto coverId;
	
	@JsonProperty("Cover Information")
	private List<List<Map<String, Object>>> coverdetals;

}
