package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackBranchByProductRes {

	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("BranchName")
	private String branchName;
	@JsonProperty("ProductList")
	private List<RenewalTrackProductRes> productList;
}
