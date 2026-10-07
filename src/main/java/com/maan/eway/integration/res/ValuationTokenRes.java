package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ValuationTokenRes {
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("token")
	private String token;
}
