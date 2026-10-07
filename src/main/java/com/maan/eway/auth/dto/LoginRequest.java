package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginRequest {

	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("Password")
	private String password;
	
	@JsonProperty("ReLoginKey")
	private String reLoginKey;
	
	@JsonProperty("IpAddress")
	private String ipAddress;
	
/*	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("CompanyId")
	private String companyId; */
	/// ONly for encryptoin;
	@JsonProperty("e")
	private String encryptionkey;
	

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;

}
