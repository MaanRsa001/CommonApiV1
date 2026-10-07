package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionWiseSumInsuredRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("ProductSuminsuredDetails")
	private Object productSuminsuredDetails;
	
	@JsonProperty("ProductId")
	private String productId;
	
}
