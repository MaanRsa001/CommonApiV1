package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RtGetProductsReq {

	@JsonProperty("ProductCode")
	private String productCode;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("CompanyId")
	private String companyId;
	
}
