package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChangeEndoStatusReq {

	
	@JsonProperty("QuoteNo")
	private String quoteNo;
		
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
}
