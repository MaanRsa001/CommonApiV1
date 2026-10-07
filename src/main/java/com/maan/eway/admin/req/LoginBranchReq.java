package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginBranchReq {

	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
