package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackByProductResByDivision {
	@JsonProperty("DivisionCode")
	private String divisionCode;
	
	@JsonProperty("DivisionName")
	private String divisionName;
	
	@JsonProperty("TotalProductCount")
	private String totalProductCount;

	@JsonProperty("TotalPolicyCount")
	private String totalPolicyCount;
	
	@JsonProperty("ProductList")
	private List<RenewalTrackProductResByDivision> productList;
}
