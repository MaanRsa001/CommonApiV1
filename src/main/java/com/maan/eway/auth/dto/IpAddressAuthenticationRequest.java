package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IpAddressAuthenticationRequest {

	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("IpAddress")
	private String ipAddress;
	
}
