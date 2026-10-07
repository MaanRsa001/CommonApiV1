package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetMachineryContentReq {
	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("QuoteNo")
	private String quoteNo;
}
