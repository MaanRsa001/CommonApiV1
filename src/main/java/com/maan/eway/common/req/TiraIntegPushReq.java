package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TiraIntegPushReq {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("Token")
	private String token ;
	
	@JsonProperty("TiraFramedReq")
	private Object tiraFramedReq ;
	
}
