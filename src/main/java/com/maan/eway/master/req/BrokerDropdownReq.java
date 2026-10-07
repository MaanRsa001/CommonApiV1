package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerDropdownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("SubUserType")
	private String subUserType;
}
