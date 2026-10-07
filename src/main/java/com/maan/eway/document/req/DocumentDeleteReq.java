package com.maan.eway.document.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentDeleteReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;


	@JsonProperty("UniqueId")
	private String uniqueId;
	
	@JsonProperty("Id")
	private String Id;
	
	
}
