package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IssuerLoginGridReq {
	
	@JsonProperty("UserType")
	private String userType ;
	
	@JsonProperty("SubUserType")
	private String subUserType ;
	
	@JsonProperty("InsuranceId")
	private String companyId ;
//	
//	@JsonProperty("Limit")
//	private String limit;
//	
//	@JsonProperty("Offset")
//	private String offset ;

}
