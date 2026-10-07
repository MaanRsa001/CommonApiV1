package com.maan.eway.document.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommonDoumentRes {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	

	@JsonProperty("SectionId")
	private String sectionId;
	

	@JsonProperty("SectionName")
	private String sectionName;

	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
}
