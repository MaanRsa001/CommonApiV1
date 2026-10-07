package com.maan.eway.viewAll.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.ToString;
import lombok.Data;
@Data
@ToString
public class SectionDetailsKeyValueDto {
	
//	@JsonProperty("SectionName")
//	KeyAndValueDto SectionName;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionInformation")
	private List<KeyAndValueDto> premium;
	
	@JsonProperty("AdditionalRiskInfo1")
	private List<Map<String, Object>> addInfo1;
	
	@JsonProperty("AdditionalRiskInfo2")
	private List<Map<String, Object>> addInfo2;
	
	@JsonProperty("CoverCount")
	private int coverCount; 
	
	@JsonProperty("Covers")
	private List<CoverClassWarrantyExclusionRes> cweSetlist;
	
	@JsonProperty("BenefitsDetails")
	private List<Map<String, Object>> binifit;
	
	@JsonProperty("SectionOrder")
	private int sectionOrder;
	
	
	
	
}
