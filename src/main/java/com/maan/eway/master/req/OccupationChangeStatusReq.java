package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationChangeStatusReq {

	@JsonProperty("OccupationId")
	private String occupationId;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	

	@JsonProperty("ProductId")
	private String productId;
}
