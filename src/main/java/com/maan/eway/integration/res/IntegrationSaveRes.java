package com.maan.eway.integration.res;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class IntegrationSaveRes {

	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("Message")
	private String message;

	@JsonProperty("ErrorMessage")
	private String errorMessage;
	
	@JsonProperty("PWsResponsetype")
	private String pWsResponseType;
	
	@JsonProperty("PremiaPolicyNo")
	private String premiaPolicyNo;

	@JsonProperty("PWsError")
	private String pWsError;
}
