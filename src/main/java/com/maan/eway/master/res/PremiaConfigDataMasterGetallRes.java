package com.maan.eway.master.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data

public class PremiaConfigDataMasterGetallRes {
	@JsonProperty("PremiaId")
	private String premiaId;

	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	

	
	@JsonProperty("PremiaConfigDataMasterListRes")
	private List<PremiaConfigDataMasterListRes> columnList;

}
