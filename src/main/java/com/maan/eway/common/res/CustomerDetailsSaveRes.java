package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerDetailsSaveRes {

	@JsonProperty("Response")
	private String response ;
	
	@JsonProperty("CustomerId")
	private String customerId ;
	
	@JsonProperty("RequestId")
	private String requestId ;
	
}
