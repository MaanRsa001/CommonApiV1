package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionReq {

	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CoverId")
	private Integer coverId;
	
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
	

	@JsonProperty("TermsId")
	private String termsId;
	

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("CoverIds")
	private List<String> coverIds;
	
}
