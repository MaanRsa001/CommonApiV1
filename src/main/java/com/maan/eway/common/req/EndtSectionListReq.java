package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndtSectionListReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
		
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
}
