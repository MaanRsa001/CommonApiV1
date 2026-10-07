package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentMasterDropDownRes {

	
	
	@JsonProperty("Code")
	private String code;

	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("Type")
	private String type;
		
}
