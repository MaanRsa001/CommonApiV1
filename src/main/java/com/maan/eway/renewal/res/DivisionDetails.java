package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DivisionDetails {
	@JsonProperty("DivisionCode")
	private String divisionCode;

	@JsonProperty("DivisionName")
	private String divisionName;

	@JsonProperty("TotalPolicyCount")
	private String totalPolicycount;

	@JsonProperty("TotalPremium")
	private String totalPremium;

	@JsonProperty("Success")
	private String successCount;

	@JsonProperty("Pending")
	private String pendingCount;

	@JsonProperty("Lost")
	private String lostCount;
	
	@JsonProperty("SuccessRate")
	private String successRate;
}
