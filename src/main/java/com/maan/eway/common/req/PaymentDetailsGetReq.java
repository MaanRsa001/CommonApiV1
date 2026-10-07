package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentDetailsGetReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("MerchantReference")
	private String merchantReference;
	
	@JsonProperty("SearchKey")
	private String searchKey;
	
	@JsonProperty("KeyValue")
	private String keyValue;
	
}
