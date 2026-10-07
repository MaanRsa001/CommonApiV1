package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ProductStructureMasterRes {
	
	@JsonProperty("Code")
	private Integer sectionid;

	@JsonProperty("CodeDesc")
	private String sectionName;
	
	@JsonProperty("Status")
	private String Status;
	
	@JsonProperty("IndustryType")
	private String IndustryType;
	
    @JsonProperty("CodeDescLocal")
	private String localCodeDesc;
	
}
