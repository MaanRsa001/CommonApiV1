package com.maan.eway.embedded.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimDetailsReq {
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("AccidentDate")
	private String accidentDate;
	
	@JsonProperty("ClaimType")
	private String claimType;

}
