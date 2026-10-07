package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ForgetPasswordReq {


	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("EmailId")
	private String emailId;
}
