package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class ClaimIntimationGetAllREq {

	 @JsonProperty("ProductId")
	 private String productId;
	 
	 @JsonProperty("CompanyId")
	 private String companyId;
}
