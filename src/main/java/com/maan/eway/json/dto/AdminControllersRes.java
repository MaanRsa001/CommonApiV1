package com.maan.eway.json.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;

import lombok.Data;

@Data
public class AdminControllersRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	@JsonProperty("Result")
	private Object commonResponse;
	
	@JsonProperty("ErroCode")
	private int erroCode;
	
	
}
