package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonPolicyDetailsReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("ApplicatonId")
	private String applicationId ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("CompanyId")
	private String companyId ;
	
	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("RequestReferenceNo")
	private String RequestReferenceNo ;
}
