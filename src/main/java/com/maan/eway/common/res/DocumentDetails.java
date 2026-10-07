package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentDetails {

	@JsonProperty("DocumentTitle")
	private String documentTitle;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("SectionId")
	private String sectionId;
}
