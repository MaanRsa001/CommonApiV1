package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TinyUrlGetReq {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("Type")
	private String type;
}
