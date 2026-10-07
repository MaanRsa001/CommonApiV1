package com.maan.eway.admin.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AttacheIssuerBranchReq {


	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("AttachedBranches")
	private List<String> attachedBranches ;
}
