package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TracesRemovedReq {
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
}
