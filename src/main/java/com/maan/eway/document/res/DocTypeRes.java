package com.maan.eway.document.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocTypeRes {


	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("AiValidation")
	private String aiValidation;
	@JsonProperty("Mandatory")
	private String mandatory;
	
	
}
