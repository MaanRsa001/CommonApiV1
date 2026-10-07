package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReportBrokerInfoRes {

	@JsonProperty("BrokerName")
	private String brokerName;
	
	@JsonProperty("BrokerBranchName")
	private String brokerBranchName;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
}
