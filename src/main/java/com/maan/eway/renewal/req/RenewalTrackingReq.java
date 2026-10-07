package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackingReq {

	@JsonProperty("RegionName")
	private String regionName;
	
	@JsonProperty("Branch")
	private String branch;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("SourceCode")
	private String sourceCode;
	
	@JsonProperty("DivisionCode")
	private String divisionCode;
}
