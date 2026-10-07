package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferalCriteriaRes {

	@JsonProperty("ReferalId")
	private Integer referalId ;
	
	@JsonProperty("ReferalName")
	private String referalName ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode ;
	
	@JsonProperty("BranchName")
	private String branchName ;
	
	@JsonProperty("ReferalDesc")
	private String referalDesc ;
}
