package com.maan.eway.renewal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RtProductRes {

	@JsonProperty("ProductCode")
	private String polProductCode;
	@JsonProperty("ProductName")
	private String productName;
	@JsonProperty("BranchName")
	private String branchName;
}
