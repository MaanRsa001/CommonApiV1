package com.maan.eway.reinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReInsuranceCommonRes {

	@JsonProperty("Message")
	private String message;

	@JsonProperty("IsError")
	private Boolean isError;
}
