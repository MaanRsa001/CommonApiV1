package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginEncryptResponse {

	@JsonProperty("UserId")
	private String userName;
	@JsonProperty("Password")
	private String password;
	@JsonProperty("LoginType")
	private String loginType;
	@JsonProperty("BranchCode")
	private String branchcode;
	@JsonProperty("ClientRefNo")
	private String cleintRefNo;
	@JsonProperty("LeadId")
	private String leadId;
	@JsonProperty("ClientTypeId")
	private String clientTypeId;
}
