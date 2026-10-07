package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RtProductReq {

	@JsonProperty("CompanyId")
	private String companyId;
	@JsonProperty("BranchCode")
	private String branchCode;
}
