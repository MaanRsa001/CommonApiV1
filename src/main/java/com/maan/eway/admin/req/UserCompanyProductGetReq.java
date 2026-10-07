package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserCompanyProductGetReq {

	@JsonProperty("LoginId")
	private String loginId ;
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	@JsonProperty("OaCode")
	private String oaCode;
}
