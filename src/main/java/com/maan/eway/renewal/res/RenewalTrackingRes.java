package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackingRes {
	@JsonProperty("CompletedRenewal")
	private Integer completedRenewal;
	@JsonProperty("PendingRenewal")
	private Integer pendingRenewal;
	@JsonProperty("Due")
	private Integer due;
	@JsonProperty("TotalPolicyCount")
	private Integer totalPolicyCount;
	@JsonProperty("Lost")
	private Integer lost;
	@JsonProperty("CompletionRate")
	private String completionRate;
	@JsonProperty("PendingRate")
	private String pendingRate;
	@JsonProperty("LostRate")
	private String lostRate;
	@JsonProperty("SourceName")
	private String sourceName;
	@JsonProperty("RenewalDetails")
	private List<RenewalTrackingDetails> renewalDetails;
}
