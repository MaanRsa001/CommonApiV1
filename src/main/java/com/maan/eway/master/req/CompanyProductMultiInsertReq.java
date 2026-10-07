package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyProductMultiInsertReq {

	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("InsuranceId")
	private String companyId;
		
	@JsonProperty("CreatedBy")
	private String createdBy;
	
}
