package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginBranchRes {

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
}
