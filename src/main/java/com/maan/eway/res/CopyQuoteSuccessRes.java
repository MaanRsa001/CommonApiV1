package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CopyQuoteSuccessRes {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("Eservice")
	private Object commonResponse;
	
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	
	
	
}
