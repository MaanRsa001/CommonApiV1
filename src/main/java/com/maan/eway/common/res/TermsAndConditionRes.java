package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TermsAndConditionRes {


	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("WarrantyList")
	private List<WarrantyRes> warrantyRes;

	
	@JsonProperty("ClausesList")
	private List<ClausesRes> clausesRes;

	@JsonProperty("ExclusionList")
	private List<ExclusionRes> exclusionRes;

	/*
	@JsonProperty("WarrateList")
	private List<WarrateRes> warrateRes;
	*/
	
	@JsonProperty("ExcessList")
	private List<ExcessRes> excessRes;
}
