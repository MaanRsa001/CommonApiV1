package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverGetReq {
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("LoginId")
	private String loginId;
	
}
