package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductDetails {
	@JsonProperty("ProductCode")
	private String productCode;
	@JsonProperty("ProductName")
	private String productName;
	@JsonProperty("ProductCount")
	private String productCount;
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
