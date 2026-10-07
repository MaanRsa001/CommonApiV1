package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetExistingBrokerListRes {

	@JsonProperty("Code")
	private String code;
	@JsonProperty("CodeDesc")
	private String codeDesc;

	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;

}
