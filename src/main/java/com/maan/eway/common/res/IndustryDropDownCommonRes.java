package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.error.Error;
import com.maan.eway.res.IndustryDropDownRes;

import lombok.Data;

@Data
public class IndustryDropDownCommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")	
	private Boolean isError;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;

	//Dynamic
	@JsonProperty("Result")
	private List<IndustryDropDownRes> commonResponse;
	
	@JsonProperty("ErroCode")
	private int erroCode;
}
