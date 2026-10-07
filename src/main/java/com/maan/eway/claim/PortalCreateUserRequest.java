package com.maan.eway.claim;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortalCreateUserRequest {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("PassWord")
	private String password;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("PhoneNo")
	private String phoneNo;
	
	@JsonProperty("UserTypeId")
	private String userTypeId;
	
	@JsonProperty("SubUserType")
	private String subUserType;
	
	@JsonProperty("FirstName")
	private String firstName;
	
	@JsonProperty("LastName")
	private String lastName;
	

}

