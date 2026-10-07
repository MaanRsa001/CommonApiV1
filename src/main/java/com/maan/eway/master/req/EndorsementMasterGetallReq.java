package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsementMasterGetallReq {

	
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("CompanyId")
	private String companyId;
		
	@JsonProperty("EndtTypeCategoryId")
	private String endtTypeCategoryId;

	@JsonProperty("LoginId")
	private String loginId;


}
