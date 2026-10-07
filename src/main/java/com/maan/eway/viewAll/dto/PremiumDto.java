package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class PremiumDto {
	
	@JsonProperty("Service")
	private String service;

	@JsonProperty("Airservice")
	private String airservice;
	
	@JsonProperty("Levies")
	private String levies;
	
	@JsonProperty("EmergencyFund")
	private String emergencyFund;
	
	@JsonProperty("Vat")
	private String vat;
	
	@JsonProperty("TotalPremium")
	private String totalPremium;
}
