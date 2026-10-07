package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveProductDropDownReq {

	@JsonProperty("OaCode")
	private String oaCode;


	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

}
