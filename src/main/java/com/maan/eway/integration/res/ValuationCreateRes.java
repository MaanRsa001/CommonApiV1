package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationCreateRes {
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("requestId")
	private String requestId;
}
