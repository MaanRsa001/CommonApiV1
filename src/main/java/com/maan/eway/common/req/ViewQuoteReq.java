package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewQuoteReq {

	@JsonProperty("QuoteNo")
	private String quoteNo ;
	
	@JsonProperty("Type")
	private String type ;
	
	@JsonProperty("EndtTypeId")
	private String endtTypeId ;
}
