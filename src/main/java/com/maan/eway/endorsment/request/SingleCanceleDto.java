package com.maan.eway.endorsment.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SingleCanceleDto {
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	@JsonProperty("RefundAmount")
	private String refund;
	@JsonProperty("RegistrationNumber")
	private String registrationNumber;
	@JsonProperty("EndtPremium")
	private String endtPremium;
	@JsonProperty("EndtPremiumTax")
	private String endtPremiumTax;

}
