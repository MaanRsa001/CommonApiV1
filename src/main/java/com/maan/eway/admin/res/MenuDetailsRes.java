package com.maan.eway.admin.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class MenuDetailsRes {

	
	@JsonProperty("Response")
	private String response;

	@JsonProperty("SuccessId")
	private Integer successId;
}
