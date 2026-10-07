package com.maan.eway.notification.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NotifGetByQuoteNoReq {

	@JsonProperty("InsuranceId")
	private String  insuranceId ;
	
	@JsonProperty("ProductId")
	private String  productId ;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
}
