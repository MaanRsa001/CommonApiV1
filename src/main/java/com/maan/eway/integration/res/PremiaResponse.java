package com.maan.eway.integration.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiaResponse {

	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("PremiaPolicyNo")
	private String premiaPolicyNo;
}
