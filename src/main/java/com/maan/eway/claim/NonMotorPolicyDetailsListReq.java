package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NonMotorPolicyDetailsListReq {

	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("Value")
	private String value;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
}
