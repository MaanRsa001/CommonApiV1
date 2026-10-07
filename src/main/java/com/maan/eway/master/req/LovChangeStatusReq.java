package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LovChangeStatusReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;

	
	@JsonProperty("ItemType")
	private String itemType;
	
	@JsonProperty("ItemCode")
	private String itemCode;

	@JsonProperty("Status")
	private String status;
}
