package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerActiveGridReq {

	@JsonProperty("UserType")
	private String userType ;
	
	@JsonProperty("SubUserType")
	private String subUserType ;
	
	
//	@JsonProperty("Limit")
//	private String limit;
//	
//	@JsonProperty("Offset")
//	private String offset ;

}
