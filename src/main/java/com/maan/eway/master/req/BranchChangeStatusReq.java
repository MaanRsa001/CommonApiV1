package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BranchChangeStatusReq {

	 @JsonProperty("BranchCode")
	 private String branchCode;
	 
	 @JsonProperty("InsuranceId")
	 private String companyId;
	 
	 
	 @JsonProperty("Status")
	 private String status;
}
