package com.maan.eway.auth.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;

import lombok.Data;

@Data
public class CommonLoginRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
	
	@JsonProperty("ChangePasswordYn")	
	private String changePasswordYn;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	//Dynamic
	@JsonProperty("Result")
	private Object commonResponse;
	
	@JsonProperty("ErroCode")
	private int erroCode;
	
	@JsonProperty("AdditionalInfo")
	private Map<String,Object> additionalInfo;
/*	@JsonProperty("AdditionalData")
	private DefaultAllResponse defaultValue; */
	
}
