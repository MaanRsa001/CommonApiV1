package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductsBySourceRes {
	@JsonProperty("SourceCode")
	private String sourceCode;
	@JsonProperty("SourceName")
	private String sourceName;
	@JsonProperty("TotalPolicyCount")
	private String totalPolicyCount;
	@JsonProperty("ProductDetails")
	private List<ProductDetails> productDetails;
}
