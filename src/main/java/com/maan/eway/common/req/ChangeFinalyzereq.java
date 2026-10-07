package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ChangeFinalyzereq {
	

	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("FinalizeYn")
	private String finalizeYn;

}
