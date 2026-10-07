package com.maan.eway.reinsurance.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReInsuranceSectionDetails {

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("CoverDetails")
	private List<ReInsuranceCoversDetail> coverList;
}
