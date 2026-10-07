package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentInfoGetReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PaymentId")
	private String paymentId;
}
