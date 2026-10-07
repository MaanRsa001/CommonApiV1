package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionInsertReq {

	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("LocationId")
    private String locationId;  
 
    @JsonProperty("CoverId")
    private String coverId; 

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	
	@JsonProperty("RiskId")
	private String riskId;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("LocationList")
    private List<LocationTCReq> locationList;
	
	
	@JsonProperty("TermsAndConditionReq")
	private List<TermsAndConditionListReq> termsAndConditionReq;
	
	@JsonProperty("ExcessReq")
	private List<ExcessReq> excessReq;
	
}
