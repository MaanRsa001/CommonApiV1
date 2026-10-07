package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortfolioSearchReq {

	@JsonProperty("SearchBy")
	private String searchBy;
	
	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
}
