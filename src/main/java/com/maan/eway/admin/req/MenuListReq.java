package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MenuListReq {

	@JsonProperty("UserType")
	private String userType ;
	
	@JsonProperty("SubUserType")
	private String subUserType ;
	
	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
}
