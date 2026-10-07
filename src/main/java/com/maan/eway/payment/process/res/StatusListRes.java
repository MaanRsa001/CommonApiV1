package com.maan.eway.payment.process.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatusListRes {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("InceptionDate")
	private String inceptionDate;
	
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("PaymentType")
	private String paymentType;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("Type")
	private String type;
	
}
