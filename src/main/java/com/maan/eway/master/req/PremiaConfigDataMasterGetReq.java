package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data

public class PremiaConfigDataMasterGetReq {
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
	
	@JsonProperty("ColumnId")
	private String columnId;
	

}
