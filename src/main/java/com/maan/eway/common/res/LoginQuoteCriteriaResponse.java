package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginQuoteCriteriaResponse {
	@JsonProperty("ProductId")
	private String productId ; 
	@JsonProperty("ProductName")
	private String productName ; 
	@JsonProperty("LastQuoteDate")
	private String lastQuoteDate;
//	@JsonFormat(pattern = "DD/MM/YYYY")
//	@JsonProperty("LastPolicyDate")
//	private String lastPolicyDate;
//	@JsonProperty("Premium")
//	private Double premium;
//	@JsonProperty("TotalCommission")
//	private Double totalCommission;
	@JsonProperty("QuoteCount")
	private Long quoteCount;
//	@JsonProperty("PolicyCount")
//	private Long policyCount;
//	@JsonProperty("EndtCount")
	private Long endtsCount;

}
