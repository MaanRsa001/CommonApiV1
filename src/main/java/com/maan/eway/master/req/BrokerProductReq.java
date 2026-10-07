package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerProductReq {

	@JsonProperty("LoginId")
	private String loginId ;
	
	@JsonProperty("InsuranceId")
	private String insuranceId ;
	
}
