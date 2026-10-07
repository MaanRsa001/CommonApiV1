package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MedMalDropDownReq {

	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
//	@JsonProperty("BranchCode")
//	private String branchCode;
}
