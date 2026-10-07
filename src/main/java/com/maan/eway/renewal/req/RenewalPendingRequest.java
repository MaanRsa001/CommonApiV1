package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalPendingRequest {

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ApplicationId")
	private String applicationId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("ProductId")
	private String productId;
	

}
