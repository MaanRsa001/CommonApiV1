package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackProductResByDivision {

	@JsonProperty("ProductCode")
	private String productCode;
	@JsonProperty("ProductName")
	private String productName;
	@JsonProperty("ProductCount")
	private String productCount;
	@JsonProperty("TotalPremium")
	private String totalPremium;
	@JsonProperty("AgentList")
	private List<RenewalTrackAgentResByProduct> agentList;
}
