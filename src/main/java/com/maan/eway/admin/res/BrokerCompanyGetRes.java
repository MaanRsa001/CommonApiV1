package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCompanyGetRes {

	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("CompanyName")
	private String companyName ;
	
	
	@JsonProperty("AttachedBranches")
	private List<BrokerBranchGetRes> attachedBranches ;
}
