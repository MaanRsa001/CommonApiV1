package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackAgentResByProduct2 {

	@JsonProperty("SourceCode")
	private String sourceCode;
	@JsonProperty("SourceName")
	private String sourceName;
	@JsonProperty("SourceCount")
	private String sourceCount;
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
