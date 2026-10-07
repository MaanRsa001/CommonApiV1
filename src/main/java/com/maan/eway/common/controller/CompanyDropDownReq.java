package com.maan.eway.common.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyDropDownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	@JsonProperty("BranchCode")
	private String branchCode ;
}
