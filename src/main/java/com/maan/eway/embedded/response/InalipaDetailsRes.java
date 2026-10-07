package com.maan.eway.embedded.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InalipaDetailsRes {
	
	@JsonProperty("Response")
	private List<InalipaDetailsRes1> commonResponse;
	
	@JsonProperty("Message")
	private String message;
	
	@JsonProperty("IsError")
	private boolean isError;
	
	@JsonProperty("ErrorMessage")
	private String errorMessage;
	

}
