package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CuurencyDropDownRes {


	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;
	@JsonProperty("ExchangeRate")
	private String exchangeRate;
	@JsonProperty("MinRate")
	private String minRate;
	@JsonProperty("MaxRate")
	private String maxRate;
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
}
