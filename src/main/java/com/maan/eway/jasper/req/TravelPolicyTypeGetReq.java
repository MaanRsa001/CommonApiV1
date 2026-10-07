package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TravelPolicyTypeGetReq {
	@JsonProperty("PlanTypeId")
    private String    planTypeId    ;
	@JsonProperty("PolicyTypeId")
    private String policyTypeId    ;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

//	@JsonProperty("BranchCode")
//	private String branchCode;
	
	@JsonProperty("Status")
    private String status    ;
}
