package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateDropdown {

	@JsonProperty("Code")
	private String Code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	
	
	
	@JsonProperty("Status")
	private String status;
	
}
