package com.maan.eway.renewal.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackProductRes {

	@JsonProperty("ProductCode")
	private String prodcode;
	@JsonProperty("ProductName")
	private String prodName;
	@JsonProperty("TotalRenewalNoOfPolicies")
	private Long totalRenewalNoOfPolicies;
	@JsonProperty("CompletedRenewal")
	private Long completedRenewal;
	@JsonProperty("PendingRenewal")
	private Long pendingRenewal;
	@JsonProperty("Due")
	private Long due;
	@JsonProperty("Lost")
	private Long lost;
	@JsonProperty("CompletionRate")
	private BigDecimal completionRate;
	@JsonProperty("PendingRate")
	private BigDecimal pendingRate;
	@JsonProperty("LostRate")
	private BigDecimal lostRate;
	
}
