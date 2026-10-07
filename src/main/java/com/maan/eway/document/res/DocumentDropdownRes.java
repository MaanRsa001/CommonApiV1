package com.maan.eway.document.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentDropdownRes {

	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	

	
}
