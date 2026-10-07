package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerReferalCompniesRes {
	
	@JsonProperty("InsuranceId")
	private String  insuranceId;  
	@JsonProperty("BranchName")
	private String  branchName;  
	@JsonProperty("BranchCode")
	private String  branchCode;  
	
	@JsonProperty("AttachedReferals")
	private List<IssuerReferalGetRes> attachedReferals ;

}
