package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardChart {
	@JsonProperty("Date")
	private String date;
	@JsonProperty("QuoteCount")	
	private BigDecimal quoteCount;
	@JsonProperty("PolicyCount")
	private BigDecimal policyCount;
	@JsonProperty("QuotePremium")	
	private BigDecimal quotePremium;
	@JsonProperty("PolicyPremium")
	private BigDecimal policyPremium;
	
	@JsonProperty("Currency")
	private String currency;
	 
}
