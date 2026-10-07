package com.maan.eway.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class UserOtp {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("LoginId")
	private String loginId;
	/*
	@JsonProperty("CreateUser")
	private Boolean createUser;
	
	@JsonProperty("CustomerId")
	private Boolean customerId;
	*/
	@JsonProperty("TemplateName")
	private Boolean templateName;
	
	@JsonProperty("OtpUser")
	private OtpUser user;
	
}
 
