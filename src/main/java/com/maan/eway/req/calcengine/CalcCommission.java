package com.maan.eway.req.calcengine;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CalcCommission {

	@JsonProperty("InsuranceId") 
	private String insuranceId;
	@JsonProperty("BranchCode") 
	private String branchCode;
	@JsonProperty("AgencyCode") 
	private String agencyCode;
	
	 @JsonProperty("SectionId") 
	 private String sectionId;

	 @JsonProperty("ProductId") 
	 private String productId;
	 
	 
	 
	 
		@JsonProperty("CreatedBy")
		private String createdBy;
		
		
		@JsonProperty("QuoteNo")
		private String quoteno;
		
		@JsonProperty("PolicyNo")
		private String policyNo;
}
