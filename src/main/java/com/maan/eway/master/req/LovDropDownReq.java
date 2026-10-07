package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LovDropDownReq {

	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("ProductId")
	private String productId;
	

	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
	@JsonProperty("ManufactureAge")
	private String manufactureAge;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("Usage")
	private String usage;


}
