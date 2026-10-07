package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorMasterGetAllReq {


	@JsonProperty("ModuleId")
	private String moduleId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("BranchCode")
	private String branchCode;
}
