package com.maan.eway.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValidateOtp {
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("AgencyCode")
	private String agencyCode;
	
	@JsonProperty("OtpToken")
	private Long otpToken;
	
	@JsonProperty("UserOTP")
	private String userOtp;
	
	@JsonProperty("CreateUser")
	private Boolean createUser;
	
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("ReferenceNo")
	private String referenceNo;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	//@JsonProperty("AgencyCode")
	private String createdAgencyCode;
	
	// online watsapp.
	 @JsonProperty("UserMobileNo")
	 private String mobileNo;
	 @JsonProperty("UserMobileCode")
	 private String mobileCode;	
	
}
