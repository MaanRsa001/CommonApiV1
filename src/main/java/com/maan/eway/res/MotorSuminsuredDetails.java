package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorSuminsuredDetails {

	@JsonProperty("AccessoriesSuminsured")
	private String accessoriesSuminsured;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
}
