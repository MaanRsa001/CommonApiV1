package com.maan.eway.reinsurance.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class RIBrokerRes {


	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;

	@JsonProperty("Type")
	private String type;
	
}
