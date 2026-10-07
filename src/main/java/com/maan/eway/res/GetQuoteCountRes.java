package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetQuoteCountRes {

	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("QuoteCount")
	private String quoteCount ;
	
	@JsonProperty("PolicyCount")
	private String policyCount ;
	
	@JsonProperty("Premium")
	private String premium ;
	
	@JsonProperty("OverallPremium")
	private String overallPremium ;
}
