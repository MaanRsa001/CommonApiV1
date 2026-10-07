package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerBranchesReq {

	 @JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BrokerBranchCode")
	private List<String> brokerBranchCode ;
	
}
