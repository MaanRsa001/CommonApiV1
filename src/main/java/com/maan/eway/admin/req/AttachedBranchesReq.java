package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttachedBranchesReq {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("AttachedBranches")
	private List<BrokerBranchesReq> attachedBranches ;
}
