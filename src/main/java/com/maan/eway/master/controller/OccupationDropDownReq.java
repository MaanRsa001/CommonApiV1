package com.maan.eway.master.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OccupationDropDownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("TitleType")
	private String titletype;
	
	@JsonProperty("CategoryId")
	private String categoryid;
}
