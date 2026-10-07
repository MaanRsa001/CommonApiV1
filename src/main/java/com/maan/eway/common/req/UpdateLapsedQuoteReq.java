package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdateLapsedQuoteReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;	
	
	@JsonProperty("InsuranceId")
	private String companyId;
		
	@JsonProperty("ProductId")
	private String productId;


	
}
