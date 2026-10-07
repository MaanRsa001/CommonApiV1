package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TinyUrlGetRes {


	@JsonProperty("TinyUrl")
	private String tinyUrl ;
	

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	

	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("OverAllPremiumFc")
	private String overAllPremiumFc ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
}
