package com.maan.eway.viewAll.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoverClassWarrantyExclusionRes {
	
//	@JsonProperty("HEADER")
//	KeyAndValueDto coverId;
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("ColumnCount")
	private String columnCount;
	
	@JsonProperty("CoverInformation")
	private List<Map<String, Object>> coverdetals;
	
	@JsonProperty("ClausesDetails")
	private List<Map<String, Object>> condition;
	
	@JsonProperty("WarrantyDetails")
	private List<Map<String, Object>> warranty;
	
	@JsonProperty("ExclusionDetails")
	private List<Map<String, Object>> exclusionList;
	
	@JsonProperty("ExcessDetails")
	private List<Map<String, Object>> excess;
	
	

}
