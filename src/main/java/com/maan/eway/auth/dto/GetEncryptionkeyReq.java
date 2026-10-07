package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetEncryptionkeyReq {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("InsuranceId")
	private String InsuranceId;
	
	@JsonProperty("TinyUrlId")
	private String tinyUrlId;
	
	@JsonProperty("TinyGroupId")
	private String tinyGroupId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("IsEmail")
	private String isEmail;
	
}
