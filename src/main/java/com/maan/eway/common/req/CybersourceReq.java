package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CybersourceReq {
	
	@JsonProperty("QuoteId")
	private String quoteId;
	
	@JsonProperty("MerchantId")
	private String merchantId;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
}
