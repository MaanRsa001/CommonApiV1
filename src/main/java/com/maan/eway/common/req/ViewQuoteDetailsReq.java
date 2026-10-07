package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ViewQuoteDetailsReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
}
