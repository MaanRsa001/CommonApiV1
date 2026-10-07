package com.maan.eway.renewal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetCustomersByBrokerReq {

	@JsonProperty("SourceCode")
	private String sourceCode;
	
}
