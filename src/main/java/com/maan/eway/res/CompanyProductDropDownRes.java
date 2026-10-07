package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyProductDropDownRes {

	@JsonProperty("Code")
	private String code ;
	
	@JsonProperty("CodeDesc")
	private String codeDesc ;
	
	@JsonProperty("ProductIconId")
	private String productIconId;
	
	@JsonProperty("ProductIconName")
	private String productIconName;
}
