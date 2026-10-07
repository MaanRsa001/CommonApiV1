package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubUrbDropDown {


	@JsonProperty("Code")
	private String Code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	@JsonProperty("AreaGroup")
	private String areaGroup;
	
	@JsonProperty("Status")
	private String status;
}
