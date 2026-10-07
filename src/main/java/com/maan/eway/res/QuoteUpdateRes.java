package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteUpdateRes {

	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerId")
	private String customerId;
}
