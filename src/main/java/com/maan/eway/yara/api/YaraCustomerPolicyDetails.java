package com.maan.eway.yara.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YaraCustomerPolicyDetails {

	@JsonProperty("PolicyEndDate")
	private String policyEndDate;
	
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;
	
//	@JsonProperty("Currency")
//	private String currency;
	
	@JsonProperty("PaymentMode")
	private String paymentMode;
}
