package com.maan.eway.master.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeSubCoverMasterGetAllReq implements Serializable {

    private static final long serialVersionUID = 1L;

    
    
    @JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("PlanTypeId")
	private String planTypeId;
	
	@JsonProperty("CoverId")
	private String coverId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("BranchCode")
	private String branchCode;

}
