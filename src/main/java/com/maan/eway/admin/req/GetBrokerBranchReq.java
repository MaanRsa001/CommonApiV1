package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetBrokerBranchReq {

	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode ;
}
