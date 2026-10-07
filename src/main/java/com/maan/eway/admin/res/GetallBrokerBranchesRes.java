package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallBrokerBranchesRes {

	
	@JsonProperty("BrokerBranchCode")
	private String brokerBranchCode;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
}
