package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	
}
