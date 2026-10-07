package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndtDependantMasterGetReq {

	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;

	@JsonProperty("DependantFieldId")
	private String dependantFieldId;

	
}
