package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallBrokerBranchesReq {

	@JsonProperty("BrokerCode")
	private String brokerCode;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
