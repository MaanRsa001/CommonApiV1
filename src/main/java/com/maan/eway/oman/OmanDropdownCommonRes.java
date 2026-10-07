package com.maan.eway.oman;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;

import lombok.Data;


@Data
public class OmanDropdownCommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	//Dynamic
	@JsonProperty("Result")
	private List<OmanDropDownRes> commonResponse;
	
	@JsonProperty("ErroCode")
	private int erroCode;
}
