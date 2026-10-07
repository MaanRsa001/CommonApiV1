package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DeleteOldQuoteReq {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("ProductId")
	private String productId;
	
}
