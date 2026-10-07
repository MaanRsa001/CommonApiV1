package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CountRes {
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("QuoteTotalCount")
	private Long quotetotalCount;

	@JsonProperty("PolicyTotalCount")
	private Long policytotalCount;

	@JsonProperty("EndtTotalCount")
	private Long endttotalCount;

		
}
