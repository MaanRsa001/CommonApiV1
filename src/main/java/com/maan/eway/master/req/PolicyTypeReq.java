package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeReq {
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("PolicyTypeId")
	private String policyTypeId;
	
}
