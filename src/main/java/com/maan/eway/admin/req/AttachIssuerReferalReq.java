package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachIssuerReferalReq {

	@JsonProperty("LoginId")
	private String loginId ;
	@JsonProperty("InsuranceId")
	private String  insuranceId;  
	
	@JsonProperty("BranchCode")
	private String  branchCode;  
	
	@JsonProperty("AttachedReferals")
	private List<AttachReferalReq> attachedReferals;
}
