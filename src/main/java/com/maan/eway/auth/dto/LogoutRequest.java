package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LogoutRequest {

	@JsonProperty("LoginId")
	private String userId;
	
	@JsonProperty("Token")
	private String token;

}
