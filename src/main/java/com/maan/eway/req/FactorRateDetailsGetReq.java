package com.maan.eway.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateDetailsGetReq {


	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
}
