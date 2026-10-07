package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionGetReq {

	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
		
	@JsonProperty("Id")
	private String id;
	

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
		
}
