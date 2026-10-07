package com.maan.eway.document.ai.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetVehicleDamegeReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RegistrationNo")
	private String regNo;
	
	@JsonProperty("InsuranceId")
	private String companyId;
}
