package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NewQuoteRes {

	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("PolicyNo")
	private String policyNo;

	@JsonProperty("AdditionalInfoYN")
    private String additionalInfoYN;
	
}
