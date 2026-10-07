package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteUpdateRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
		
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("Response")
	private String response;
}
