package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SuccessRes {

	@JsonProperty("Response")
	private String response;

	@JsonProperty("SuccessId")
	private String successId;
	
}
