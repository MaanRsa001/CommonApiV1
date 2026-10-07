package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReferalResponse {


	@JsonProperty("Response")
	private String response;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("ReferalRemarks")
	private String referalRemarks;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Referral")
	private String referral;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;

}
