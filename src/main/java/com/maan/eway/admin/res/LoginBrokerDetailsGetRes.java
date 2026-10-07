package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginBrokerDetailsGetRes {

	
	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("OaCode")
	private String oaCode ;
	
	@JsonProperty("BranchCodes")
	private String branchCodes ;
	
	@JsonProperty("CompanyId")
	private String companyId;
}
