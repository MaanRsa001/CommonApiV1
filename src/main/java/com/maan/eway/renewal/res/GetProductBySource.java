package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public  class GetProductBySource {
	@JsonProperty("ProductCode")
	private String productCode;
	@JsonProperty("ProductName")
	private String productName;
	@JsonProperty("ProductCount")
	private String productCount;
	@JsonProperty("TotalPremium")
	private String totalPremium;
	@JsonProperty("PolicyList")
	private List<PolicyDet> policyList;

}
