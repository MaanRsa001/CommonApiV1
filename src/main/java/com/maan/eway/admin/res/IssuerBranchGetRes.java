package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerBranchGetRes {

	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BranchName")
	private String branchName ;
	
	@JsonProperty("RegionCode")
	private String regionCode ;
	
	@JsonProperty("RegionName")
	private String regionName ;
}
