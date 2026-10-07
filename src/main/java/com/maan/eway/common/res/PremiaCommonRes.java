package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;
import com.maan.eway.integration.res.IntegrationSaveRes;

import lombok.Data;

@Data
public class PremiaCommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	//Dynamic
	@JsonProperty("Result")
	private IntegrationSaveRes commonResponse;
	
	@JsonProperty("ErroCode")
	private int erroCode;

	

/*	@JsonProperty("AdditionalData")
	private DefaultAllResponse defaultValue; */
}
