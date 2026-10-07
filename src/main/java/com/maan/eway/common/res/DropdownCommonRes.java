package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;
import com.maan.eway.res.DropDownRes;

import lombok.Data;

@Data
public class DropdownCommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	//Dynamic
	@JsonProperty("Result")
	private List<DropDownRes> commonResponse;
	
	@JsonProperty("ErroCode")
	private int erroCode;

	

/*	@JsonProperty("AdditionalData")
	private DefaultAllResponse defaultValue; */
}
