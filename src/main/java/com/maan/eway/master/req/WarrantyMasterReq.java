package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WarrantyMasterReq {

	@JsonProperty("WarrantyId")
	private String warrantyId;
	

	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	
}
