package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortalCreateBranchRequest {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("LoginId")
	private String loginId;

}