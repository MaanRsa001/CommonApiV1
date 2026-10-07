package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OriginatingCountryDropdownReq {

	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("OpenCoverNo")
	private String openCoverNo;
	@JsonProperty("OriginationCountryCode")
	private String originationCountryCode;
	@JsonProperty("BranchCode")
	private String branchCode;
	
}
