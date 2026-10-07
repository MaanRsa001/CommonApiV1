package com.maan.eway.integration.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiaGetReq {
private static final long serialVersionUID = 1L;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
}
