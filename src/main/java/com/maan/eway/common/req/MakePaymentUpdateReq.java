package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MakePaymentUpdateReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Status")
	private String status;
	
}
