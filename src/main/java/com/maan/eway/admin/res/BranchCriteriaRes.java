package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchCriteriaRes {

	@JsonProperty("BranchCode")
	private String branchCode ;
	@JsonProperty("BranchName")
	private String branchName ;
	@JsonProperty("RegionCode")
	private String regionCode ;
	@JsonProperty("RegionCode")
	private String regionName ;
	@JsonProperty("InsuranceId")
	private String companyId ;
	@JsonProperty("CompanyName")
	private String companyName ;
}
