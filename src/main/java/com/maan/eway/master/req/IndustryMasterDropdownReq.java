package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IndustryMasterDropdownReq {

	@JsonProperty("InsuranceId")
	private String companyId ;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("CategoryId")
	private String categoryId;
	
}
