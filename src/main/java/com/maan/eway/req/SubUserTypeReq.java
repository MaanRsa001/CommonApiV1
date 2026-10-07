package com.maan.eway.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubUserTypeReq {

	@JsonProperty("UserType")
	private String userType ;
	
	@JsonProperty("LoginId")
	private String loginId ;
}
