package com.maan.eway.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteCountCriteriaRes {

	@JsonProperty("ProductId")
	private Integer productId ;
	
	@JsonProperty("ProductName")
	private String productName ;
	
	@JsonProperty("QuoteCount")
	private Long quoteCount ;
	
	@JsonProperty("PolicyCount")
	private Long policyCount ;
	
	@JsonProperty("Premium")
	private Double premium;
	
	@JsonProperty("OverallPremium")
	private Double overallPremium;
}
