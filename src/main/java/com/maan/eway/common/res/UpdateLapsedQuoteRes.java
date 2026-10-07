package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateLapsedQuoteRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Message")
	private String message;
	
	
}
