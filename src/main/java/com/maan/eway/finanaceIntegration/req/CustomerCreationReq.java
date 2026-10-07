package com.maan.eway.finanaceIntegration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerCreationReq {

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("PolCustCode")
	private String polCustCode;
	
	private boolean thirdpartyStatus;
	

	
}
