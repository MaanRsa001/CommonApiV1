package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetDocListReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
}
