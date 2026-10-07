package com.maan.eway.payment.process.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StatusListReq {

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
}
