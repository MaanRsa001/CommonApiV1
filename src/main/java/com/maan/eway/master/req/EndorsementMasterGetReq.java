package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementMasterGetReq {

	
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CompanyId")
	private String companyId;
		
	@JsonProperty("EndtTypeCategoryId")
	private String endtTypeCategoryId;
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId;
	

}
